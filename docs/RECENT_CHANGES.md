# Recent Changes

## 2025-01-XX - UI Improvements & Documentation Restructure

### Documentation Organization
- Created `docs/` folder to organize all documentation
- Moved the following files to `docs/`:
  - `CHANGELOG.md`
  - `CONVERT_TO_PDF.md`
  - `DOKUMENTASI_PROYEK.md`
  - `USE_CASE_DETAIL_DOSEN_ADMIN.md`
  - `USE_CASE_DIAGRAM.md`
- Simplified `README.md` to be more concise with reference to detailed docs

### UI/UX Improvements

#### CSS Redesign
- Implemented CSS variables for consistent theming
- Removed excessive color variations for cleaner design
- Unified color palette:
  - Primary: `#3b82f6` (blue)
  - Success: `#10b981` (green)
  - Danger: `#ef4444` (red)
  - Warning: `#f59e0b` (amber)
- Improved border radius consistency (6px for cards, 4px for small elements)
- Enhanced hover states for better user feedback

#### Dark Mode Support
- Added dark mode CSS variables
- Implemented theme toggle button in header (moon/sun icon)
- Dark mode persists across dialogs and secondary windows
- Proper text contrast in both light and dark themes
- Dark mode styling for all components:
  - Cards and containers
  - Buttons and inputs
  - Tables and lists
  - Progress bars
  - Scrollbars

#### Controller Updates
- Added `themeToggleBtn` to `MainController`
- Implemented `toggleDarkMode()` method
- Dark mode state applies to all opened windows
- Theme toggle uses simple icon switch (🌙/☀)

#### FXML Updates
- Added theme toggle button to `MainView.fxml` header
- Button positioned after refresh button for easy access

### Technical Improvements
- Better code formatting and organization in `MainController`
- Consistent spacing and indentation
- Improved maintainability with CSS variables
- Scalable theme system for future enhancements

### Benefits
- Reduced eye strain with dark mode option
- More professional and consistent design
- Better organized documentation structure
- Easier to maintain and update themes
- Improved user experience with clear visual hierarchy