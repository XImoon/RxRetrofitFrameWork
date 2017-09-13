package cn.ximoon.rxretrofitframework.core.convert;

import org.simpleframework.xml.Serializer;

import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

final class XmlRequestBodyConverter<T> implements Converter<T, RequestBody> {
  private static final MediaType MEDIA_TYPE = MediaType.parse("application/xml; charset=UTF-8");
  private static final String CHARSET = "UTF-8";

  private final Serializer serializer;

  XmlRequestBodyConverter(Serializer serializer) {
    this.serializer = serializer;
  }

  @Override public RequestBody convert(T value) throws IOException {
    Buffer buffer = new Buffer();
    try {
      OutputStreamWriter osw = new OutputStreamWriter(buffer.outputStream(), CHARSET);
      serializer.write(value, osw);
      osw.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
  }
}