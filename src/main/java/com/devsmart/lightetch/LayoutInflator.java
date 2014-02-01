package com.devsmart.lightetch;


import com.devsmart.lightetch.widgets.LinearLayout;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class LayoutInflator {

    private static class FieldAssign {
        Field mField;
        Object mObject;

        public FieldAssign(Object obj, Field field) {
            mObject = obj;
            mField = field;
        }

        public void assign(String value) throws IllegalAccessException {
            Class<?> fieldType = mField.getType();
            if(String.class.isAssignableFrom(fieldType)){
                mField.set(mObject, value);
            } else if(fieldType.equals(int.class)){
                mField.setInt(mObject, Integer.parseInt(value));
            } else if(fieldType.equals(float.class)){
                mField.setFloat(mObject, Float.parseFloat(value));
            }
        }
    }

    private static class LayoutInflatorInstance {

        static HashMap<String, Class<? extends View>> mNameMap = new HashMap<String, Class<? extends View>>();
        static {
            mNameMap.put("View", View.class);
            mNameMap.put("ViewGroup", ViewGroup.class);
            mNameMap.put("LinearLayout", LinearLayout.class);

        }

        private final XmlPullParser mParser;
        View mParent;
        Class<? extends ViewGroup.LayoutParams> mParentLayoutParamsType;

        public LayoutInflatorInstance(XmlPullParser parser) {
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

            View newView = (View) viewclass.newInstance();

            if(mParentLayoutParamsType == null){
                mParentLayoutParamsType = ViewGroup.LayoutParams.class;
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
                }
            }

            return newView;
        }



        private void assignAttributes(View newView) throws IOException, XmlPullParserException, IllegalAccessException {

            HashMap<String, FieldAssign> viewFields = new HashMap<String, FieldAssign>();
            for(Field f : newView.getClass().getFields()){
                int modifier = f.getModifiers();
                if((modifier & Modifier.STATIC) > 0 || (modifier & Modifier.FINAL) > 0) {

                } else {
                    viewFields.put(f.getName(), new FieldAssign(newView, f));
                }
            }
            for(Field f : newView.mLayoutParams.getClass().getFields()){
                int modifier = f.getModifiers();
                if((modifier & Modifier.STATIC) > 0 || (modifier & Modifier.FINAL) > 0) {

                } else {
                    viewFields.put(f.getName(), new FieldAssign(newView.mLayoutParams, f));
                }
            }

            final int numAttributes = mParser.getAttributeCount();
            for(int i=0;i<numAttributes;i++){
                String key = mParser.getAttributeName(i);
                String value = mParser.getAttributeValue(i);

                FieldAssign assign = viewFields.get(key);
                if(assign != null){
                    assign.assign(value);
                }
            }
        }
    }

    public static View inflate(InputStream in) throws Exception {
        View retval = null;
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(in, null);

            LayoutInflatorInstance inflator = new LayoutInflatorInstance(parser);
            retval = inflator.parse();

        } finally {
            in.close();
        }

        return retval;
    }


}
