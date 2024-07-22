package util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        Optional<Duration> value = Optional.ofNullable(duration);
        if (value.isPresent()) {
            jsonWriter.value(value.get().toMinutes());
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        try {
            String value = jsonReader.nextString();
            if (value.equals("null")) {
                return null;
            }
            return Duration.ofMinutes(Long.parseLong(value));
        } catch (IllegalStateException exception) {
            jsonReader.nextNull();
            return null;
        }
    }
}

