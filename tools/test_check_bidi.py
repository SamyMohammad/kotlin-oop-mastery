import unittest
from check_bidi import lint_text

class BidiLint(unittest.TestCase):
    def errs(self, text):
        return lint_text(text, "t.md")

    def test_clean_arabic_line(self):
        self.assertEqual(self.errs("الـ `class` هو القالب.\n"), [])

    def test_english_only_line_ok(self):
        self.assertEqual(self.errs("Kotlin classes are final by default.\n"), [])

    def test_rlm_rejected(self):
        self.assertTrue(self.errs("‏الكلاس\n"))

    def test_escape_text_rejected(self):
        self.assertTrue(self.errs("الكلاس \\u200f هنا\n"))

    def test_line_starting_with_backtick_rejected(self):
        self.assertTrue(self.errs("`sealed` يعني مقفول\n"))

    def test_bold_english_start_rejected(self):
        self.assertTrue(self.errs("- **Modifier** هو interface\n"))

    def test_heading_and_bullet_markers_skipped(self):
        self.assertEqual(self.errs("## الوراثة\n- الكلاس `open`\n1. أول خطوة\n"), [])

    def test_arrow_in_arabic_rejected(self):
        self.assertTrue(self.errs("الكلاس -> الأوبجكت\n"))

    def test_left_arrow_allowed(self):
        self.assertEqual(self.errs("الكلاس ← الأوبجكت\n"), [])

    def test_code_fence_skipped(self):
        self.assertEqual(self.errs("```\n`x` -> الكود\n```\n"), [])

    def test_table_cell_must_start_arabic(self):
        self.assertTrue(self.errs("| `open` كلاس | تمام |\n"))
        self.assertEqual(self.errs("| الكلمة `open` | تمام |\n"), [])

    def test_details_summary_line(self):
        self.assertEqual(self.errs("<details><summary>الإجابة</summary>\n"), [])

    def test_checkbox_marker_skipped(self):
        self.assertEqual(self.errs("- [ ] ملف `Exercises.kt` بيطبع ALL PASS\n"), [])

if __name__ == "__main__":
    unittest.main()
