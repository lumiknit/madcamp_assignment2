package io.madcamp.jh.madcamp_assignment2;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;

import java.util.ArrayList;

public class SpannableFuzzyFinder {
    private Context context;

    public SpannableFuzzyFinder(Context context) {
        this.context = context;
    }

    public void refilterContacts(ArrayList<Contact> origin, ArrayList<ListViewAdapter.Item> result, String pattern) {
        char[] p = pattern.toCharArray();
        result.clear();
        for(int i = 0; i < origin.size(); i++) {
            Contact x = origin.get(i);
            SpannableStringBuilder f_name = fuzzyFind(x.name, p);
            SpannableStringBuilder f_number = fuzzyFind(x.phoneNumber, p);
            if(f_name != null || f_number != null) {
                if(f_name == null) f_name = new SpannableStringBuilder(x.name);
                if(f_number == null) f_number = new SpannableStringBuilder(x.phoneNumber);
                result.add(new ListViewAdapter.Item(i, f_name, f_number));
            }
        }
    }

    public SpannableStringBuilder fuzzyFind(String s, char[] p) {
        SpannableStringBuilder res = new SpannableStringBuilder(s);
        int i;
        int j = 0;
        for(i = 0; i < s.length() && j < p.length; i++) {
            if(fuzzyEqual(s.charAt(i), p[j])) {
                res.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                        i, i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                res.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)),
                        i, i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                j++;
            }
        }
        if(j < p.length) return null;
        return res;
    }

    public static boolean fuzzyEqual(char a, char p) {
        if(Character.toLowerCase(a) == Character.toLowerCase(p)) /* 영어 ㅕ */
            return true;
        else if(0xac00 <= a && a <= 0xd7a3) { /* 한글 */
            int a_rel = a - 0xac00;
            int jong = a_rel % 28;
            int jung = a_rel / 28 % 21;
            int cho = a_rel / 28 / 21;

            if(0x1100 <= p && p <= 0x11f9) {
                if (p <= 0x1112 && cho == p - 0x1100) return true;
                else return (0x1161 <= p && p <= 0x1175 && jung == p - 0x1161);
            } else if(0x3131 <= p && p <= 0x3163) {
                if(p <= 0x314e && cho == hangulExtToCho(p - 0x3131)) return true;
                else return (0x314f <= p && jung == p - 0x314f);
            }
        }
        return false;
    }

    public static int hangulExtToCho(int v) {
        int r = 0;
        switch(v + 1) {
            case 0x1e: case 0x1d: case 0x1c: case 0x1b: case 0x1a: case 0x19: case 0x18: case 0x17: case 0x16: case 0x15:
            case 0x14: r++;
            case 0x13: case 0x12: case 0x11:
            case 0x10: r++; case 0x0f: r++; case 0x0e: r++;case 0x0d: r++;case 0x0c: r++;case 0x0b: r++;case 0x0a: r++;
            case 0x09: case 0x08: case 0x07:
            case 0x06: r++; case 0x05: r++;
            case 0x04:
            case 0x03: r++;
        }
        return v - r;
    }
}
