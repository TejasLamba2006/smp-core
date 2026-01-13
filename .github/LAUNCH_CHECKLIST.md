# Pre-Release Checklist for Open Source

Use this checklist before making the repository public and submitting your Modrinth appeal.

## 🔍 Pre-Flight Checks

### Repository Content

- [ ] All sensitive information removed (API keys, passwords, etc.)
- [ ] `.gitignore` is comprehensive
- [ ] LICENSE file is present and correct
- [ ] README is updated with open source info
- [ ] CHANGELOG.md is up to date

### Documentation

- [ ] All documentation is accurate
- [ ] Links to GitHub repo work
- [ ] Documentation site is deployed
- [ ] No TODOs or placeholders in user-facing docs

### Code Quality

- [ ] Code compiles without errors
- [ ] No debug statements or test code
- [ ] No hardcoded values that should be in config
- [ ] All features are properly documented

### GitHub Setup

- [ ] Issue templates are working
- [ ] PR template is working
- [ ] Workflows are configured
- [ ] Branch protection rules set
- [ ] Repository description is clear

## 🚀 Making Repository Public

### Step 1: Final Review

1. [ ] Review all files one more time
2. [ ] Test build: `mvn clean package`
3. [ ] Check for any personal information
4. [ ] Verify all workflows will work (no private dependencies)

### Step 2: Repository Settings

1. [ ] Go to Settings > General
2. [ ] Scroll to "Danger Zone"
3. [ ] Click "Change visibility"
4. [ ] Select "Make public"
5. [ ] Type repository name to confirm
6. [ ] Click "I understand, make this repository public"

### Step 3: Enable Features

1. [ ] Settings > Features > Enable Issues
2. [ ] Settings > Features > Enable Discussions (optional)
3. [ ] Settings > Security > Enable CodeQL
4. [ ] Settings > Security > Enable Dependabot alerts
5. [ ] Settings > Actions > Allow all actions

### Step 4: Branch Protection

1. [ ] Settings > Branches > Add rule
2. [ ] Branch name pattern: `main`
3. [ ] Enable: "Require pull request reviews before merging"
4. [ ] Enable: "Require status checks to pass"
5. [ ] Enable: "Require branches to be up to date"
6. [ ] Save changes

### Step 5: CodeRabbit Setup

1. [ ] Go to <https://coderabbit.ai/>
2. [ ] Sign in with GitHub
3. [ ] Install CodeRabbit app
4. [ ] Grant access to `smp-core` repository
5. [ ] Verify `.coderabbit.yaml` is detected

## 📤 Modrinth Appeal

### Before Submitting Appeal

- [ ] Repository is public
- [ ] All documentation is complete
- [ ] GitHub shows commit history
- [ ] CodeQL scan has run successfully

### Appeal Message Template

```
Subject: Appeal for SMP Core Project Rejection - Original Work Verification

Dear Modrinth Moderation Team,

I am writing to appeal the rejection of my project "SMP Core" (Project ID: GH4H8ndx). 
I understand the concern regarding potential reuploads.

CLARIFICATION:

This is original work, not a reupload. I have now made the project fully open source 
to demonstrate authenticity:

1. **GitHub Repository**: https://github.com/TejasLamba2006/smp-core
   - Complete commit history from [FIRST_COMMIT_DATE]
   - Original package structure: com.tejaslamba.smpcore
   - Professional development setup with CI/CD
   - Active development with 50+ commits

2. **Proof of Original Development**:
   - Full source code available
   - Commit history shows iterative development
   - Unique implementation patterns
   - Original documentation site: https://smpcore.tejaslamba.com

3. **Name Explanation**:
   - "SMP Core" is a generic/descriptive name for SMP plugins
   - I was unaware of the BuiltByBit project when naming mine
   - My implementation and architecture are completely original

4. **Open Source Transparency**:
   - Public repository with MIT-like license
   - Community contribution guidelines
   - Professional issue tracking and PR process
   - CodeQL security scanning enabled

VERIFICATION OFFERED:
- Review commit history
- Examine source code structure
- Compare implementations
- Video call if needed

Thank you for reconsidering this appeal. I'm committed to being a responsible 
member of the Modrinth community.

Best regards,
Tejas Lamba
GitHub: TejasLamba2006
Repository: https://github.com/TejasLamba2006/smp-core
Website: https://smpcore.tejaslamba.com
```

### Submit Appeal

1. [ ] Go to Modrinth support server
2. [ ] Use appeal template above
3. [ ] Include GitHub repository link
4. [ ] Include first commit date
5. [ ] Be polite and professional
6. [ ] Wait for response

## 📣 Announcement

### After Repository is Public

1. [ ] Create a GitHub Release
   - Tag: v1.1.0
   - Title: "SMP Core v1.1.0 - Now Open Source!"
   - Description: From CHANGELOG.md + open source announcement

2. [ ] Update Modrinth Description
   - Add "Open Source" badge/mention
   - Link to GitHub repository
   - Mention contribution guidelines

3. [ ] Announce on Discord

   ```
   🎉 **Big News!** 🎉
   
   SMP Core is now fully open source! 
   
   🔗 GitHub: https://github.com/TejasLamba2006/smp-core
   📚 Docs: https://smpcore.tejaslamba.com
   
   You can now:
   - View the source code
   - Report bugs on GitHub
   - Contribute features
   - Submit pull requests
   
   Let's build this together! 💪
   ```

4. [ ] Tweet/Social Media (if applicable)

## 🎯 Post-Launch Tasks

### Week 1

- [ ] Monitor issues for questions
- [ ] Respond to initial feedback
- [ ] Fix any reported bugs
- [ ] Welcome first contributors

### Ongoing

- [ ] Review and merge pull requests
- [ ] Keep documentation updated
- [ ] Release updates regularly
- [ ] Engage with community

## ⚠️ Important Reminders

- **License**: Your code is under Custom Non-Redistribution license
- **Contributors**: Will need to agree to license
- **Dependencies**: Check all are properly licensed
- **Trademarks**: Ensure no trademark issues with name
- **Privacy**: No player data collection without consent

---

## ✅ Final Checklist

- [ ] Repository is public
- [ ] All workflows passing
- [ ] Documentation is complete
- [ ] CodeRabbit is active
- [ ] Branch protection enabled
- [ ] Appeal submitted to Modrinth
- [ ] Announcement prepared

**Good luck with your open source launch! 🚀**
