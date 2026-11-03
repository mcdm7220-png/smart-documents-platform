# المنصة الذكية للمستندات — Smart Documents Platform

مشروع **Kotlin Compose Multiplatform** يبني تطبيقين من منطق واحد، ويعمل **دون إنترنت** بعد تجهيز الموارد:

| الناتج | الوحدة | الوظيفة |
|---|---|---|
| **APK** (أندرويد) | `androidApp` | تصوير مستند بالكاميرا + OCR عربي/إنجليزي + أرشفة وبحث |
| **EXE** (ويندوز) | `desktopApp` | استيراد PDF وصور + OCR + تصنيف وتسمية وأرشفة + بحث + توليد أمر إداري بـ Gemma 3n + طباعة وتصدير PDF |
| المنطق المشترك | `shared/core` | قاعدة Room + التصنيف والتسمية والبحث + واجهات المحركات + شاشة الأرشيف المشتركة |

## الحالة الحالية (صريحة)
**الكود مكتوب لكنه لم يُترجَم ولم يُجرَّب بعد** — أُنشئ في بيئة لا تصل إلى Android SDK/Maven. أول بناء على GitHub Actions قد يُظهر أخطاء ترجمة تُصلَّح بعدها. لا توجد أرقام أداء مقاسة.

## البناء
تلقائياً: كل push على `main` يشغّل `.github/workflows/build.yml` ويرفع:
- `SmartDocuments-APK` (ملف `.apk` debug)
- `SmartDocuments-EXE` (مثبّت `.exe`، ويضمّ `tesseract.exe` وبيانات `ara`/`eng`)

محلياً: `./gradlew :androidApp:assembleDebug` و`./gradlew :desktopApp:packageExe` (الأخير على ويندوز).
قبل البناء المحلي ضع `ara.traineddata` و`eng.traineddata` في `androidApp/src/main/assets/tessdata/`، وللديسكتوب `tesseract.exe` + `tessdata` في `desktopApp/resources/windows/tesseract/`.

## البنية
```
shared/core
  commonMain   Db.kt (Room)، Text.kt (تصنيف/تواريخ/تسمية/توجيه Gemma)، Engines.kt (OcrEngine/LlmEngine/PdfTools)، ui_Archive.kt
  jvmShared    Service.kt (DocumentService: استيراد→OCR→تصنيف→أرشفة، إعدادات، تنزيل النموذج) — يُترجَم لأندرويد وسطح المكتب
  androidMain  Tesseract (tesseract4android)، PdfRenderer، PdfDocument
  desktopMain  Tesseract CLI، PDFBox، عرض النص العربي بـ Java2D، الطباعة
androidApp     كاميرا (FileProvider) + رفع ملفات + أرشيف
desktopApp     استيراد، أرشيف وبحث، أمر إداري، إعدادات
```

## قواعد التشغيل
- كل البيانات محلية (Room/SQLite + ملفات). لا Firebase ولا خوادم.
- التصنيف والعنوان والتاريخ المستخرجة **اقتراحات** تظهر «بانتظار المراجعة» حتى يعتمدها المستخدم.
- لا حذف دون تأكيد. نفس الملف لا يُستورد مرتين (SHA-256).
- Gemma لا يخترع بيانات: الحقول الناقصة تبقى `........`، والمسودة قابلة للتعديل قبل الحفظ أو الطباعة.

## Gemma 3n E2B (سطح المكتب)
- الصيغة المطلوبة `.litertlm` (ملفات GGUF لا تعمل مع LiteRT-LM). الملف نحو 3.4 GB.
- يُشغَّل عبر أمر `litert-lm` (ثبّته بـ `uv tool install litert-lm` أو `pip install litert-lm`).
- التنزيل من الإعدادات يحتاج قبول شروط Gemma على Hugging Face ورمز وصول؛ أو ضع الملف يدوياً وحدّد مساره.
- صيغة الاستدعاء من وثائق LiteRT-LM ولم تُجرَّب هنا؛ المحرك خلف واجهة `LlmEngine` لتبديله بسهولة.

## حدود معروفة
- نسخة أندرويد: Gemma غير موصول بعد (التصنيف بالقواعد فقط)؛ لا استخراج نص مباشر من PDF (يُرسَم كل صفحة ثم OCR).
- PDF المُصدَّر للأوامر يحوي النص كصور (لضمان تشكيل العربية)، فلا يمكن تحديد النص داخله.
- صيغتا DOCX وXLSX غير مدعومتين بعد.
- جودة OCR العربي تعتمد على وضوح الصورة وتحتاج مراجعة بشرية.

الترخيص: انظر `LICENSE`.
