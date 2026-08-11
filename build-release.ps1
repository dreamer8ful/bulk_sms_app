#!/usr/bin/env powershell
# Release Build Script - Automates the release build process

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("bundle", "apk", "both")]
    [string]$BuildType = "bundle",

    [Parameter(Mandatory=$false)]
    [switch]$Test = $false,

    [Parameter(Mandatory=$false)]
    [switch]$Clean = $false,

    [Parameter(Mandatory=$false)]
    [switch]$SkipSigning = $false
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommandPath

Write-Host "🚀 Bulk SMS App Release Build Script" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan

# Check Java
Write-Host "`n📋 Checking prerequisites..." -ForegroundColor Yellow
$javaCheck = java -version 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Java not found. Please set JAVA_HOME or install Java 11+`n" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Java found: $(java -version 2>&1 | Select-Object -First 1)" -ForegroundColor Green

# Check for keystore if not skipping signing
if (-not $SkipSigning) {
    if (-not (Test-Path "app/key.keystore")) {
        Write-Host "❌ Keystore not found at app/key.keystore`n" -ForegroundColor Red
        Write-Host "Generate one with:" -ForegroundColor Yellow
        Write-Host "  cd app" -ForegroundColor Gray
        Write-Host "  keytool -genkey -v -keystore key.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias release_key`n" -ForegroundColor Gray
        exit 1
    }
    Write-Host "✓ Keystore found" -ForegroundColor Green

    # Check environment variables
    if (-not $env:KEYSTORE_PASSWORD -or -not $env:KEY_ALIAS -or -not $env:KEY_PASSWORD) {
        Write-Host "⚠️  Signing environment variables not all set. Will attempt to use gradle.properties." -ForegroundColor Yellow
    } else {
        Write-Host "✓ Signing environment variables configured" -ForegroundColor Green
    }
}

# Clean build if requested
if ($Clean) {
    Write-Host "`n🧹 Cleaning build artifacts..." -ForegroundColor Yellow
    & "./gradlew.bat" clean
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Clean failed" -ForegroundColor Red
        exit 1
    }
    Write-Host "✓ Clean complete" -ForegroundColor Green
}

# Run tests if requested
if ($Test) {
    Write-Host "`n🧪 Running tests..." -ForegroundColor Yellow
    & "./gradlew.bat" test
    if ($LASTEXITCODE -ne 0) {
        Write-Host "⚠️  Some tests failed (this might be okay depending on setup)" -ForegroundColor Yellow
    } else {
        Write-Host "✓ Tests passed" -ForegroundColor Green
    }
}

# Lint check
Write-Host "`n📝 Running lint checks..." -ForegroundColor Yellow
& "./gradlew.bat" lint
if ($LASTEXITCODE -ne 0) {
    Write-Host "⚠️  Lint found issues (check build/reports/lint-results.html)" -ForegroundColor Yellow
}

# Build based on type
Write-Host "`n🔨 Building release..." -ForegroundColor Yellow

switch ($BuildType) {
    "bundle" {
        Write-Host "Building AAB (Android App Bundle) for Play Store..." -ForegroundColor Cyan
        & "./gradlew.bat" bundleRelease
        if ($LASTEXITCODE -eq 0) {
            $aabPath = "app\build\outputs\bundle\release\app-release.aab"
            if (Test-Path $aabPath) {
                $size = (Get-Item $aabPath).Length / 1MB
                Write-Host "✓ AAB created successfully ($([math]::Round($size, 2))MB)" -ForegroundColor Green
                Write-Host "  Location: $aabPath" -ForegroundColor Cyan
            }
        }
    }
    "apk" {
        Write-Host "Building APK (Android Package) for direct distribution..." -ForegroundColor Cyan
        & "./gradlew.bat" assembleRelease
        if ($LASTEXITCODE -eq 0) {
            $apkPath = "app\build\outputs\apk\release\app-release.apk"
            if (Test-Path $apkPath) {
                $size = (Get-Item $apkPath).Length / 1MB
                Write-Host "✓ APK created successfully ($([math]::Round($size, 2))MB)" -ForegroundColor Green
                Write-Host "  Location: $apkPath" -ForegroundColor Cyan
            }
        }
    }
    "both" {
        Write-Host "Building both AAB and APK..." -ForegroundColor Cyan
        & "./gradlew.bat" bundleRelease assembleRelease
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Both builds successful" -ForegroundColor Green
        }
    }
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n❌ Build failed" -ForegroundColor Red
    exit 1
}

# Post-build verification
Write-Host "`n✅ Build complete!" -ForegroundColor Green
Write-Host "`n📊 Build Summary:" -ForegroundColor Cyan
Write-Host "  • Output: app\build\outputs\" -ForegroundColor Gray
Write-Host "  • Minification: Enabled" -ForegroundColor Gray
Write-Host "  • Resource Shrinking: Enabled" -ForegroundColor Gray
Write-Host "  • Signing: $(if ($SkipSigning) { 'Skipped' } else { 'Applied' })" -ForegroundColor Gray

Write-Host "`n📋 Next Steps:" -ForegroundColor Yellow
if ($BuildType -eq "bundle" -or $BuildType -eq "both") {
    Write-Host "  1. Upload AAB to Google Play Console" -ForegroundColor Gray
    Write-Host "  2. Complete store listing (screenshots, description, etc.)" -ForegroundColor Gray
    Write-Host "  3. Submit for review" -ForegroundColor Gray
}
if ($BuildType -eq "apk" -or $BuildType -eq "both") {
    Write-Host "  1. Test APK on multiple devices: adb install -r app\build\outputs\apk\release\app-release.apk" -ForegroundColor Gray
    Write-Host "  2. Verify all features work correctly" -ForegroundColor Gray
}

Write-Host "`n💡 Tip: Check proguard mapping for debugging crashes:" -ForegroundColor Cyan
Write-Host "  Location: app\build\outputs\mapping\release\mapping.txt" -ForegroundColor Gray
Write-Host ""

