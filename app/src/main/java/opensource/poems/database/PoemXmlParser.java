
package opensource.poems.database;

import java.util.ArrayList;
import java.util.List;

import opensource.poems.utils.Logger;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Xml;

public class PoemXmlParser {

    private static final Logger LOGGER = new Logger(PoemXmlParser.class);

    private static final String POEM_FILENAME = "poem.xml";

    private static String safeNextText(XmlPullParser parser) throws Exception {

        String result = parser.nextText();

        if (parser.getEventType() != XmlPullParser.END_TAG) {

            parser.nextTag();

        }

        return result;
    }

    public static List<PoemBean> parse(Context context) throws Exception {
        List<PoemBean> poetryItems = null;
        PoemBean item = null;

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(context.getAssets().open(POEM_FILENAME), "UTF-8");

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    poetryItems = new ArrayList<PoemBean>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("item")) {
                        item = new PoemBean();
                        item.id = Long.parseLong(parser.getAttributeValue(0));
                        item.name = parser.getAttributeValue(1);
                        item.poet = parser.getAttributeValue(2);
                        item.type = parser.getAttributeValue(3);
                    } else if (parser.getName().equals("value")) {
                        item.value = safeNextText(parser);
                        LOGGER.d("value:" + item.value);
                    } else if (parser.getName().equals("remark")) {
                        item.remark = safeNextText(parser);
                        LOGGER.d("remark:" + item.remark);
                    } else if (parser.getName().equals("translation")) {
                        item.translation = safeNextText(parser);
                        LOGGER.d("translation:" + item.translation);
                    } else if (parser.getName().equals("analysis")) {
                        item.analysis = safeNextText(parser);
                        LOGGER.d("analysis:" + item.analysis);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("item")) {
                        poetryItems.add(item);
                        item = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return poetryItems;
    }
}
