package com.sf.LuaEditor;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.*;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import com.sf.ALuaGuide.ExceptionsHandle;
import com.sf.ALuaGuide.R;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderEditor extends android.support.v7.widget.AppCompatEditText {
    private static final Pattern PATTERN_NUMBERS = Pattern.compile(
            "\\b(\\d*[.]?\\d+)\\b");

    private static final Pattern PATTERN_KEYWORDS = Pattern.compile(
            "\\b(" +
                    "and|break|do|else|elseif|end|false|for|function|goto|if|" +
                    "in|local|nil|not|or|repeat|return|then|true|until|while" +
                    ")\\b");

    private static final Pattern PATTERN_BUILTINS = Pattern.compile(
            "\\b(pcall|print|rawequal|rawget|rawlen|rawset|require|select|self|" +

                    "TextView|EditText|Button|ImageButton|ImageView|CheckBox|RadioButton|ToggleButton|" +
                    "Switch|ListView|GridView|PageView|ExpandableListView|Spinner|SeekBar|ScrollView|" +
                    "ProgressBar|RatingBar|DatePicker|TimePicker|NumberPicker|LinearLayout|HorizontalScrollView|" +
                    "AbsoluteLayout|FrameLayout|RelativeLayout|CardView|RadioGroup|GridLayout|" +

                    "getmetatable|ipairs|load|loadfile|loadstring|module|next|pairs|" +
                    "setmetatable|tointeger|tonumber|tostring|type|unpack|xpcall|" +
                    "TextView|assert|collectgarbage|dofile|error|findtable|" +
                    "coroutine|debug|io|luajava|math|os|package|string|table|utf8|" +
                    "setmetatable|setupvalue|setuservalue|traceback|upvalueid|upvaluejoin|sethook|" +
                    "close|flush|input|lines|open|output|popen|read|stderr|stdin|stdout|tmpfile|type|" +
                    "loaded|luapath|new|newInstance|package|tostring|abs|acos|asin|atan|ceil|cos|" +
                    "pow|rad|random|randomseed|sin|sinh|sqrt|tan|tanh|tointeger|type|ult|task|thread|timer|" +
                    "clock|date|difftime|execute|exit|getenv|remove|rename|setlocale|time|tmpname|" +
                    "byte|char|dump|find|format|gfind|gmatch|gsub|len|lower|match|pack|packsize|rep|" +
                    "activity|call|compile|dump|each|enum|import|loadbitmap|loadlayout|loadmenu|set|" +
                    "setlocal|write)\\b");
    private static final Pattern PATTERN_COMMENTS = Pattern.compile(
            "--\\[\\[(?:.|[\\n\\r])*?\\]\\]|--.*");

    private static final Pattern OPERATORS = Pattern.compile("[\\p{P}+~$`^=]");

    private final Handler updateHandler = new Handler();
    private OnTextChangedListener onTextChangedListener;
    private int updateDelay;
    private boolean dirty = false;
    private boolean modified = true;
    private int colorNumber;
    private int colorKeyword;
    private int colorBuiltin;
    private int colorComment;
    private final Runnable updateRunnable =
            new Runnable() {
                @Override
                public void run() {
                    Editable e = getText();

                    if (onTextChangedListener != null)
                        onTextChangedListener.onTextChanged(
                                e.toString());

                    highlightWithoutChange(e);
                }
            };
    private int tabWidth = 0;
    private Context context;

    public ShaderEditor(Context context) {
        super(context);
        init(context);
    }

    public ShaderEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private static void clearSpans(Editable e) {
        {
            ForegroundColorSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    ForegroundColorSpan.class);

            for (int n = spans.length; n-- > 0; )
                e.removeSpan(spans[n]);
        }

        {
            StyleSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    StyleSpan.class);

            for (int n = spans.length; n-- > 0; )
                e.removeSpan(spans[n]);
        }
    }

    public boolean isModified() {
        return dirty;
    }

    private void init(Context context) {
        this.context = context;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean("nightMode", false)) {
            setTextColor(Color.parseColor("#e0e0e0"));
        } else {
            setTextColor(Color.parseColor("#424242"));
        }
        this.updateDelay = Integer.parseInt(sp.getString("delay", "10"));

        setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(
                            CharSequence source,
                            int start,
                            int end,
                            Spanned dest,
                            int dstart,
                            int dend) {
                        if (modified &&
                                end - start == 1 &&
                                start < source.length() &&
                                dstart < dest.length()) {
                            char c = source.charAt(start);

                            if (c == '\n')
                                return autoIndent(
                                        source,
                                        dest,
                                        dstart,
                                        dend);
                        }

                        return source;
                    }
                }});

        if (sp.getBoolean("heightLight", true)) {

            addTextChangedListener(
                    new TextWatcher() {
                        private int start = 0;
                        private int count = 0;

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            this.start = start;
                            this.count = count;
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable e) {
                            cancelUpdate();
                            convertTabs(e, start, count);

                            if (!modified)
                                return;

                            dirty = true;
                            updateHandler.postDelayed(
                                    updateRunnable,
                                    updateDelay);
                        }
                    });
        }

        setSyntaxColors(context);
    }

    private void setSyntaxColors(Context context) {
        colorNumber = ContextCompat.getColor(
                context,
                R.color.syntax_number);
        colorKeyword = ContextCompat.getColor(
                context,
                R.color.syntax_keyword);
        colorBuiltin = ContextCompat.getColor(
                context,
                R.color.syntax_builtin);
        colorComment = ContextCompat.getColor(
                context,
                R.color.syntax_comment);

    }

    private void cancelUpdate() {
        updateHandler.removeCallbacks(updateRunnable);
    }

    private void highlightWithoutChange(Editable e) {
        modified = false;
        highlight(e);
        modified = true;
    }

    private Editable highlight(Editable e) {
        try {
            clearSpans(e);

            if (e.length() == 0)
                return e;

            for (Matcher m = OPERATORS.matcher(e); m.find(); )
                e.setSpan(new ForegroundColorSpan(Color.parseColor("#673ab7")), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            for (Matcher m = PATTERN_NUMBERS.matcher(e); m.find(); )
                e.setSpan(new ForegroundColorSpan(colorNumber), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            for (Matcher m = PATTERN_KEYWORDS.matcher(e); m.find(); ) {
                e.setSpan(new ForegroundColorSpan(colorKeyword), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                e.setSpan(new StyleSpan(Typeface.BOLD), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            for (Matcher m = PATTERN_BUILTINS.matcher(e); m.find(); )
                e.setSpan(new ForegroundColorSpan(colorBuiltin), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            for (Matcher m = Pattern.compile("\\\"(.*?)\\\"|\\\'(.*?)\\\'|\\[\\[(.*?)\\]\\]").matcher(e); m.find(); ) {
                e.setSpan(new ForegroundColorSpan(Color.parseColor("#26a69a")), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            for (Matcher m = PATTERN_COMMENTS.matcher(e); m.find(); ) {
                e.setSpan(new ForegroundColorSpan(colorComment), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                e.setSpan(new StyleSpan(Typeface.ITALIC), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        } catch (IllegalStateException error) {
            ExceptionsHandle.show(context, error.toString());
        }
        return e;
    }

    private CharSequence autoIndent(
            CharSequence source,
            Spanned dest,
            int dstart,
            int dend) {
        String indent = "";
        int istart = dstart - 1;

        boolean dataBefore = false;
        int pt = 0;

        for (; istart > -1; --istart) {
            char c = dest.charAt(istart);

            if (c == '\n')
                break;

            if (c != ' ' &&
                    c != '\t') {
                if (!dataBefore) {
                    if (c == '{' ||
                            c == '+' ||
                            c == '-' ||
                            c == '*' ||
                            c == '/' ||
                            c == '%' ||
                            c == '^' ||
                            c == '=')
                        pt--;

                    dataBefore = true;
                }

                if (c == '(')
                    --pt;
                else if (c == ')')
                    ++pt;
            }
        }

        if (istart > -1) {
            char charAtCursor = dest.charAt(dstart);
            int iend;

            for (iend = ++istart;
                 iend < dend;
                 ++iend) {
                char c = dest.charAt(iend);

                if (charAtCursor != '\n' &&
                        c == '/' &&
                        iend + 1 < dend &&
                        dest.charAt(iend) == c) {
                    iend += 2;
                    break;
                }

                if (c != ' ' &&
                        c != '\t')
                    break;
            }

            indent += dest.subSequence(istart, iend);
        }

        if (pt < 0)
            indent += "\t";

        return source + indent;
    }

    private void convertTabs(Editable e, int start, int count) {
        if (tabWidth < 1)
            return;

        String s = e.toString();

        for (int stop = start + count;
             (start = s.indexOf("\t", start)) > -1 && start < stop;
             ++start)
            e.setSpan(
                    new TabWidthSpan(),
                    start,
                    start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public interface OnTextChangedListener {
        void onTextChanged(String text);
    }

    private class TabWidthSpan extends ReplacementSpan {
        @Override
        public int getSize(
                Paint paint,
                CharSequence text,
                int start,
                int end,
                Paint.FontMetricsInt fm) {
            return tabWidth;
        }

        @Override
        public void draw(
                Canvas canvas,
                CharSequence text,
                int start,
                int end,
                float x,
                int top,
                int y,
                int bottom,
                Paint paint) {
        }
    }

    public StringBuilder format(String text) {
        int width = 2;
        StringBuilder builder = new StringBuilder();
        boolean isNewLine = true;
        LuaLexer lexer = new LuaLexer(text);
        try {
            int idt = 0;

            while (true) {
                LuaTokenTypes type = lexer.advance();
                if (type == null)
                    break;
                if (type == LuaTokenTypes.NEWLINE) {
                    isNewLine = true;
                    builder.append('\n');
                    idt = Math.max(0, idt);

                } else if (isNewLine) {
                    if (type == LuaTokenTypes.WS) {

                    } else if (type == LuaTokenTypes.ELSE) {
                        idt--;
                        builder.append(createIntdent(idt * width));
                        builder.append(lexer.yytext());
                        idt++;
                        isNewLine = false;
                    } else if (type == LuaTokenTypes.ELSEIF || type == LuaTokenTypes.END || type == LuaTokenTypes.UNTIL || type == LuaTokenTypes.RCURLY) {
                        idt--;
                        builder.append(createIntdent(idt * width));
                        builder.append(lexer.yytext());

                        isNewLine = false;
                    } else {
                        builder.append(createIntdent(idt * width));
                        builder.append(lexer.yytext());
                        idt += indent(type);
                        isNewLine = false;
                    }
                } else if (type == LuaTokenTypes.WS) {
                    builder.append(' ');
                } else {
                    builder.append(lexer.yytext());
                    idt += indent(type);
                }

            }
        } catch (IOException e) {
            ExceptionsHandle.show(context, e.toString());
        }
        return builder;
    }

    private static int indent(LuaTokenTypes t) {
        switch (t) {
            case DO:
            case FUNCTION:
            case THEN:
            case REPEAT:
            case LCURLY:
                return 1;
            case UNTIL:
            case ELSEIF:
            case END:
            case RCURLY:
                return -1;
            default:
                return 0;
        }
    }

    private static char[] createIntdent(int n) {
        if (n < 0)
            return new char[0];
        char[] idts = new char[n];
        for (int i = 0; i < n; i++)
            idts[i] = ' ';
        return idts;
    }
}
