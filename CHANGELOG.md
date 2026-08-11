# CHANGELOG - Bulk SMS Sender

All notable changes to the Bulk SMS Sender app will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.1] - 2026-07-20

### Added
- Production-ready optimized release build
- Code minification using R8/ProGuard for smaller APK size
- Comprehensive ProGuard rules for code protection
- Release signing configuration
- Pre-release documentation and guides

### Improved
- Build optimization for Play Store distribution
- Resource shrinking to remove unused assets
- Version management (build 2, version 1.1)

### Fixed
- Build configuration for production release

### Security
- Added code obfuscation
- Implemented resource shrinking
- Security configuration (.gitignore)

---

## [1.0] - Initial Release

### Added
- ✅ Bulk SMS sending to multiple recipients
- ✅ Manual phone number entry (one per line, comma, semicolon separated)
- ✅ Import contacts from device
- ✅ Import phone numbers from CSV file
- ✅ Automatic duplicate removal
- ✅ Customizable delay between sends (1-5000ms)
- ✅ Real-time progress tracking
- ✅ Sent/Failed/Delivered counts
- ✅ Failed number logging
- ✅ Message preview before sending
- ✅ Personalized messages with recipient names
- ✅ Automatic multipart SMS handling
- ✅ Delivery status confirmation
- ✅ Material Design UI
- ✅ Android 5.0+ (API 21) support
- ✅ Runtime permission handling
- ✅ Contact search/filter

### Permissions
- SEND_SMS (required)
- READ_PHONE_STATE (required)
- READ_CONTACTS (optional)

---

## Planned Features

### [1.2] (Planned)
- [ ] Message templates
- [ ] Scheduled SMS sending
- [ ] SMS sending history
- [ ] Contact groups
- [ ] Backup/restore functionality
- [ ] Dark mode support
- [ ] Multiple language support
- [ ] SMS cost estimation

### [1.3] (Planned)
- [ ] Advanced filtering
- [ ] Recipient tagging
- [ ] Message personalization variables
- [ ] Batch processing
- [ ] Analytics dashboard
- [ ] Export delivery reports

### Future
- [ ] Cloud backup
- [ ] Web dashboard
- [ ] API integration
- [ ] Webhook support

---

## Known Issues

### Version 1.1
- None reported

### Version 1.0
- ProGuard minification may require additional -keep rules for custom functionality (if added later)

---

## Deprecated

### Version 1.1
- Nothing deprecated

---

## Version Numbering

**Format**: MAJOR.MINOR (with BUILD number)

- **MAJOR**: Breaking changes, significant new features
- **MINOR**: New features, improvements, bug fixes
- **BUILD**: Internal build number (auto-incremented)

**Example**: v1.1 (Build 2)

---

## Release Process

Each release follows this process:

1. Feature development in main branch
2. Code review and testing
3. Version bump (update build.gradle.kts and CHANGELOG.md)
4. Tag release: `git tag v1.1`
5. Build release APK/AAB
6. Test on multiple devices
7. Submit to Google Play Store
8. Monitor for crashes and feedback

---

## How to Update

### For Users
- Enable auto-updates in Google Play Store
- Or manually check "Updates" in Play Store app

### For Developers
- Pull latest changes: `git pull`
- Build new version: `./gradlew bundleRelease`
- Test thoroughly
- Submit new build

---

## Version History Summary

| Version | Build | Date | Type | Status |
|---------|-------|------|------|--------|
| 1.1 | 2 | 2026-07-20 | Release | Ready |
| 1.0 | 1 | - | Release | Active |

---

## Contributing

To contribute to version updates:

1. Create a feature branch: `git checkout -b feature/name`
2. Make changes and commit: `git commit -am 'Add feature'`
3. Submit PR with updated CHANGELOG.md
4. Increment version after merge

---

## Release Notes Template

For each release, follow this format in Play Store:

```
Version X.X - YYYY-MM-DD

✨ New Features:
• Feature 1
• Feature 2

🐛 Bug Fixes:
• Fixed issue 1
• Fixed issue 2

⚡ Improvements:
• Improvement 1
• Improvement 2

🔒 Security:
• Security fix 1
```

---

## Next Release Checklist

- [ ] Update CHANGELOG.md
- [ ] Update version in build.gradle.kts
- [ ] Update version in README.md (if needed)
- [ ] Test on multiple Android versions
- [ ] Get code review
- [ ] Build release APK/AAB
- [ ] Test release build
- [ ] Tag git repository
- [ ] Write release notes
- [ ] Submit to Play Store
- [ ] Update CHANGELOG.md with store link

---

**Last Updated**: July 20, 2026  
**Current Version**: 1.1 (Build 2)  
**Maintainer**: [Your Name]

---

## Resources
- [Keep a Changelog](https://keepachangelog.com/)
- [Semantic Versioning](https://semver.org/)
- [Android Versioning](https://developer.android.com/studio/publish/versioning)

