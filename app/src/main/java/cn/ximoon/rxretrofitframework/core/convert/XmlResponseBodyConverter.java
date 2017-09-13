package cn.ximoon.rxretrofitframework.core.convert;

import org.simpleframework.xml.Serializer;

import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;

final class XmlResponseBodyConverter<T> implements Converter<ResponseBody, T> {
  private final Class<T> cls;
  private final Serializer serializer;
  private final boolean strict;

  XmlResponseBodyConverter(Class<T> cls, Serializer serializer, boolean strict) {
    this.cls = cls;
    this.serializer = serializer;
    this.strict = strict;
  }

  @Override public T convert(ResponseBody value) throws IOException {
    try {
      T read = serializer.read(cls, value.charStream(), strict);
      if (read == null) {
        throw new IllegalStateException("Could not deserialize body as " + cls);
      }
      return read;
    } catch (RuntimeException | IOException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      value.close();
    }
  }
}