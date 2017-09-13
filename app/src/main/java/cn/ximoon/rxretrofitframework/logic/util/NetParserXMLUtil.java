package cn.ximoon.rxretrofitframework.logic.util;

import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import cn.ximoon.rxretrofitframework.base.util.ConstantUtil;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by Admin on 2017/9/11.
 */

public class NetParserXMLUtil {

    private static final Persister serializer = new Persister();

    public static <E> E parserT(Class<E> clazz, ResponseBody body) throws IOException {
        try {
            E read = serializer.read(clazz, body.charStream(), false);
            if (read == null) {
                throw new IllegalStateException("Could not deserialize body as " + clazz);
            }
            return read;
        } catch (RuntimeException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            body.close();
        }
    }

    public static <E> RequestBody convert(E value) throws IOException {
        Buffer buffer = new Buffer();
        try {
            OutputStreamWriter osw = new OutputStreamWriter(buffer.outputStream(), ConstantUtil.CHARSET);
            serializer.write(value, osw);
            osw.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return RequestBody.create(ConstantUtil.MEDIA_TYPE, buffer.readByteString());
    }

    public static <E> String convertString(E value){
        Buffer buffer = new Buffer();
        try {
            OutputStreamWriter osw = new OutputStreamWriter(buffer.outputStream(), ConstantUtil.CHARSET);
            serializer.write(value, osw);
            osw.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return buffer.readByteString().string(Charset.defaultCharset());
    }
}
