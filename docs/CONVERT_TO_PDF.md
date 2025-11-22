# 📄 Panduan Convert Dokumentasi ke Format Lain

## Cara Convert Dokumentasi Markdown ke PDF/DOCX/HTML

Dokumentasi proyek Adaptive Study Planner dibuat dalam format **Markdown (.md)** yang mudah dibaca di GitHub. Namun untuk keperluan presentasi, laporan, atau tugas akademik, Anda mungkin perlu convert ke format lain.

---

## 1. Convert ke PDF

### **Option A: Menggunakan Pandoc (Recommended)**

**Install Pandoc:**
- Windows: Download dari [https://pandoc.org/installing.html](https://pandoc.org/installing.html)
- macOS: `brew install pandoc`
- Linux: `sudo apt-get install pandoc`

**Command:**
```bash
# Single file dengan Table of Contents
pandoc DOKUMENTASI_PROYEK.md -o Dokumentasi_Adaptive_Study_Planner.pdf --toc --number-sections --pdf-engine=xelatex

# Multiple files combined
pandoc DOKUMENTASI_PROYEK.md USE_CASE_DIAGRAM.md README.md -o Dokumentasi_Lengkap.pdf --toc --number-sections

# Dengan custom styling
pandoc DOKUMENTASI_PROYEK.md -o Dokumentasi_Styled.pdf --toc --number-sections --pdf-engine=xelatex -V geometry:margin=1in -V fontsize=11pt
```

### **Option B: Menggunakan Online Converter (No Install)**

1. **Markdown to PDF:**
   - [https://www.markdowntopdf.com/](https://www.markdowntopdf.com/)
   - [https://md2pdf.netlify.app/](https://md2pdf.netlify.app/)

2. **Cara:**
   - Buka website
   - Copy-paste isi DOKUMENTASI_PROYEK.md
   - Klik "Convert"
   - Download PDF

### **Option C: Menggunakan VS Code Extension**

1. Install extension "Markdown PDF" di VS Code
2. Buka file `DOKUMENTASI_PROYEK.md`
3. Klik kanan → "Markdown PDF: Export (pdf)"
4. PDF otomatis tersimpan di folder yang sama

---

## 2. Convert ke DOCX (Microsoft Word)

### **Menggunakan Pandoc:**
```bash
# Simple conversion
pandoc DOKUMENTASI_PROYEK.md -o Dokumentasi_Adaptive_Study_Planner.docx

# Dengan styling dan TOC
pandoc DOKUMENTASI_PROYEK.md -o Dokumentasi_Styled.docx --toc --number-sections --reference-doc=template.docx
```

### **Menggunakan Online:**
- [https://cloudconvert.com/md-to-docx](https://cloudconvert.com/md-to-docx)
- Upload file .md → Convert → Download .docx

---

## 3. Convert ke HTML

### **Menggunakan Pandoc:**
```bash
# Simple HTML
pandoc DOKUMENTASI_PROYEK.md -o dokumentasi.html

# HTML dengan CSS styling
pandoc DOKUMENTASI_PROYEK.md -o dokumentasi.html --self-contained --css=style.css --toc

# HTML standalone (include all assets)
pandoc DOKUMENTASI_PROYEK.md -s -o dokumentasi.html --toc --css=https://cdn.jsdelivr.net/npm/github-markdown-css@5/github-markdown.min.css
```

---

## 4. Convert ke LaTeX (untuk Academic Paper)

```bash
pandoc DOKUMENTASI_PROYEK.md -o dokumentasi.tex

# Atau langsung ke PDF via LaTeX
pandoc DOKUMENTASI_PROYEK.md -o dokumentasi.pdf --pdf-engine=pdflatex
```

---

## 5. Styling Tips untuk PDF yang Lebih Baik

### **Buat file `header.tex`:**
```latex
\usepackage{fancyhdr}
\pagestyle{fancy}
\fancyhead[L]{Adaptive Study Planner}
\fancyhead[R]{\today}
\usepackage{graphicx}
\usepackage{hyperref}
```

### **Command dengan custom header:**
```bash
pandoc DOKUMENTASI_PROYEK.md -o Dokumentasi_Professional.pdf \
  --toc \
  --number-sections \
  --include-in-header=header.tex \
  -V geometry:margin=1in \
  -V fontsize=11pt \
  -V documentclass=report \
  --pdf-engine=xelatex
```

---

## 6. Recommended Format untuk Berbagai Keperluan

| Keperluan | Format | Tool | Notes |
|-----------|--------|------|-------|
| **Presentasi ke Dosen** | PDF | Pandoc + LaTeX | Professional, numbered sections, TOC |
| **Laporan Tugas** | DOCX | Pandoc | Bisa diedit lebih lanjut di Word |
| **GitHub Repository** | Markdown | - | Keep as is, optimal untuk GitHub |
| **Website/Blog** | HTML | Pandoc | Dengan GitHub Pages atau hosting |
| **Academic Paper** | LaTeX/PDF | Pandoc + LaTeX | Untuk publikasi atau thesis |
| **Print** | PDF | Pandoc + XeLaTeX | High quality print |

---

## 7. Quick Commands (Copy-Paste Ready)

### **Untuk Presentasi Dosen (PDF Profesional):**
```bash
pandoc DOKUMENTASI_PROYEK.md -o Laporan_Tugas_PBO.pdf --toc --number-sections --pdf-engine=xelatex -V geometry:margin=1in -V fontsize=12pt -V documentclass=report
```

### **Untuk Laporan Word (Bisa Edit):**
```bash
pandoc DOKUMENTASI_PROYEK.md -o Laporan_Tugas_PBO.docx --toc --number-sections
```

### **Combine All Docs ke 1 PDF:**
```bash
pandoc DOKUMENTASI_PROYEK.md USE_CASE_DIAGRAM.md USE_CASE_DETAIL_DOSEN_ADMIN.md CHANGELOG.md -o Dokumentasi_Lengkap_Adaptive_Study_Planner.pdf --toc --number-sections --pdf-engine=xelatex
```

---

## 8. Template Cover Page (Optional)

Buat file `cover.tex`:
```latex
\begin{titlepage}
\centering
\vspace*{2cm}

{\Huge\bfseries Adaptive Study Planner\\}
\vspace{0.5cm}
{\Large Aplikasi Desktop Perencana Studi Adaptif\\}
{\Large Berbasis Teknik Spaced Repetition dan Interleaving\\}

\vspace{2cm}

{\large Disusun Oleh:\\}
{\Large\bfseries [Nama Anda]\\}
{\large NIM: [NIM Anda]\\}

\vspace{2cm}

{\large Program Studi Informatika\\}
{\large Universitas [Nama Universitas]\\}
{\large \the\year\\}

\end{titlepage}
```

**Command dengan cover:**
```bash
pandoc DOKUMENTASI_PROYEK.md -o Laporan_Final.pdf \
  --include-before-body=cover.tex \
  --toc \
  --number-sections \
  --pdf-engine=xelatex
```

---

## 9. Troubleshooting

### **Error: "pandoc not found"**
→ Install Pandoc terlebih dahulu

### **Error: "pdflatex not found"**
→ Install LaTeX distribution:
- Windows: MiKTeX ([https://miktex.org/](https://miktex.org/))
- macOS: MacTeX
- Linux: `sudo apt-get install texlive-full`

### **PDF tidak bisa dibuat**
→ Gunakan `--pdf-engine=xelatex` atau online converter

---

## 10. Hasil Akhir

Setelah convert, Anda akan punya:
- ✅ `Dokumentasi_Adaptive_Study_Planner.pdf` - Siap untuk presentasi
- ✅ `Laporan_Tugas_PBO.docx` - Bisa diedit di Word
- ✅ `dokumentasi.html` - Bisa dibuka di browser

**File-file ini siap untuk:**
- 📊 Presentasi proyek
- 📝 Laporan tugas akhir
- 🎓 Submission ke dosen
- 💼 Portfolio profesional

---

**Good luck! 🚀**

