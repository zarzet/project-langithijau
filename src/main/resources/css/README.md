# Struktur CSS - Adaptive Study Planner

File CSS telah dipecah menjadi struktur modular untuk kemudahan maintenance dan organisasi.

## Struktur Folder

```
css/
├── style.css                          (File utama - import semua file)
├── variables.css                      (Color palette & themes)
├── base.css                          (Font, reset, typography)
├── utilities.css                     (Scrollbar, separator, animations)
├── components/                       (Komponen UI)
│   ├── buttons.css                   (Semua style button)
│   ├── sidebar.css                   (Sidebar & navigation)
│   ├── cards.css                     (Cards, badges, schedule cards)
│   ├── forms.css                     (Input, checkbox, combobox, datepicker)
│   ├── widgets.css                   (Widgets & settings dialog)
│   └── tables.css                    (Table view)
└── pages/                            (Halaman spesifik)
    ├── login.css                     (Login screen)
    └── database-inspector.css        (Database inspector)
```

## Cara Penggunaan

File `style.css` sudah otomatis mengimport semua file CSS lainnya. Anda hanya perlu mengimport `style.css` di FXML:

```xml
<stylesheets>
    <URL value="@../css/style.css" />
</stylesheets>
```

## Keuntungan

- **Lebih mudah dicari**: Tahu persis di file mana style tertentu berada
- **Lebih cepat di-edit**: File lebih kecil, IDE lebih responsif
- **Lebih terorganisir**: Setiap file punya tanggung jawab yang jelas
- **Reusable**: Bisa digunakan di view lain
- **Team-friendly**: Menghindari konflik saat kolaborasi

## File Backup

File CSS original telah di-backup sebagai `style-backup-original.css` (70KB).
Jika terjadi masalah, Anda bisa mengembalikan file ini.
