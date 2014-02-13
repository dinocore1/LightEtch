package com.devsmart.lightetch;


import com.devsmart.lightetch.drawable.Rectangle;
import com.devsmart.lightetch.graphics.Color;
import com.devsmart.lightetch.widgets.LinearLayout;
import com.devsmart.lightetch.widgets.SurfaceView;
import com.devsmart.lightetch.widgets.TextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

public class LayoutInflator {

    static HashMap<String, Class<? extends View>> mNameMap = new HashMap<String, Class<? extends View>>();
    static {
        mNameMap.put("View", View.class);
        mNameMap.put("ViewGroup", ViewGroup.class);
        mNameMap.put("TextView", TextView.class);
        mNameMap.put("LinearLayout", LinearLayout.class);
        mNameMap.put("SurfaceView", SurfaceView.class);

    }

    private interface AssignRule {
        boolean assign(Object obj, String key, String value);
    }

    static ArrayList<AssignRule> sAssignRules = new ArrayList<AssignRule>();
    static {
        sAssignRules.add(new AssignRule() {
            @Override
            public boolean assign(Object obj, String key, String value) {
                boolean retval = obj instanceof ViewGroup.LayoutParams
                        && ("width".equals(key) || "height".equals(key))
                        && ("fill".equals(value) || "wrap".equals(value));
                if(retval){
                    int intval = "fill".equals(value) ?
                            ViewGroup.LayoutParams.FILL_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
                    if("width".equals(key)){
                        ((ViewGroup.LayoutParams)obj).width = intval;
                    } else {
                        ((ViewGroup.LayoutParams)obj).height = intval;
                    }
                }
                return retval;
            }
        });
        sAssignRules.add(new AssignRule() {
            @Override
            public boolean assign(Object obj, String key, String value) {
                boolean retval = obj instanceof ViewGroup.MarginLayoutParams
                        && ("margin".equals(key));
                if(retval) {
                    int intVal = Integer.parseInt(value);
                    ((ViewGroup.MarginLayoutParams)obj).marginBottom = intVal;
                    ((ViewGroup.MarginLayoutParams)obj).marginTop = intVal;
                    ((ViewGroup.MarginLayoutParams)obj).marginLeft = intVal;
                    ((ViewGroup.MarginLayoutParams)obj).marginRight = intVal;
                }
                return retval;
            }
        });
        sAssignRules.add(new AssignRule() {
            @Override
            public boolean assign(Object obj, String key, String value) {
                boolean retval = obj instanceof View && ("background".equals(key));
                if(retval){
                    Rectangle rectbg = new Rectangle();
                    rectbg.mColor = Color.parseColor(value);
                    ((View)obj).mBackground = rectbg;
                }
                return retval;
            }
        });
        sAssignRules.add(new AssignRule() {
            @Override
            public boolean assign(Object obj, String key, String value) {
                boolean retval = obj instanceof LinearLayout && "orientation".equals(key);
                if(retval){
                    int o = "vertical".equals(value) ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL;
                    ((LinearLayout)obj).orientation = o;
                }
                return retval;
            }
        });
        sAssignRules.add(new AssignRule() {
            @Override
            public boolean assign(Object obj, String key, String value) {
                boolean retval = false;
                try {
                    Field f = obj.getClass().getField(key);
                    Class<?> fieldType = f.getType();
                    if(String.class.isAssignableFrom(fieldType)){
                        f.set(obj, value);
                        retval = true;
                    } else if(fieldType.equals(int.class)){
                        f.setInt(obj, Integer.parseInt(value));
                        retval = true;
                    } else if(fieldType.equals(float.class)){
                        f.setFloat(obj, Float.parseFloat(value));
                        retval = true;
                    }
                } catch(NoSuchFieldException e) {
                } catch (IllegalAccessException e) {
                }

                return retval;
            }
        });
    }

    private final Context mContext;
    private final XmlPullParser mParser;
    View mParent;
    Class<? extends ViewGroup.LayoutParams> mParentLayoutParamsType;

    private LayoutInflator(Context context, XmlPullParser parser) {
        mContext = context;
        mParser = parser;
    }

    public View parse() throws Exception {
        View root = null;
        mParser.nextTag();
        root = readTag();
        return root;
    }

    private View readTag() throws Exception {

        String tagname = mParser.getName();

        Class<?> viewclass = mNameMap.get(tagname);
        if(viewclass == null){
            viewclass =  Class.forName(tagname);
        }
        if(!View.class.isAssignableFrom(viewclass)){
            throw new Exception("class: '" + tagname + "' is not inherit from View");
        }

        Constructor<? extends View> constructor = (Constructor<? extends View>) viewclass.getConstructor(Context.class);
        View newView = constructor.newInstance(mContext);

        if(mParentLayoutParamsType == null){
            mParentLayoutParamsType = ViewGroup.MarginLayoutParams.class;
        }
        newView.mLayoutParams = mParentLayoutParamsType.newInstance();

        assignAttributes(newView);

        //newView becomes the parent
        mParent = newView;
        for(Class<?> innerClass : mParent.getClass().getDeclaredClasses()){
            if(ViewGroup.LayoutParams.class.isAssignableFrom(innerClass)){
                mParentLayoutParamsType = (Class<? extends ViewGroup.LayoutParams>) innerClass;
                break;
            }
        }


        while(mParser.next() != XmlPullParser.END_DOCUMENT){
            switch(mParser.getEventType()){
                case XmlPullParser.START_TAG:
                    ((ViewGroup)newView).addView(readTag());
                    break;
                case XmlPullParser.END_TAG:
                    return newView;
            }
        }

        return newView;
    }



    private void assignAttributes(View newView) throws IOException, XmlPullParserException, IllegalAccessException {
        final int numAttributes = mParser.getAttributeCount();
        for(int i=0;i<numAttributes;i++){
            String key = mParser.getAttributeName(i);
            String value = mParser.getAttributeValue(i);

            if(!assignField(newView, key, value)){
                assignField(newView.mLayoutParams, key, value);
            }
        }
    }

    private static boolean assignField(Object obj, String key, String value) {
        boolean canAssign = false;
        for(AssignRule rule : sAssignRules){
            canAssign = rule.assign(obj, key, value);
            if(canAssign) {
                break;
            }
        }
        return canAssign;
    }

    public static View inflate(Context context, InputStream in) throws Exception {
        View retval = null;
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(in, null);

            LayoutInflator inflator = new LayoutInflator(context, parser);
            retval = inflator.parse();

        } finally {
            in.close();
        }

        return retval;
    }


}
