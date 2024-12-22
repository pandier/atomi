package io.github.pandier.atomi.sponge.internal.command.brigadier;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ComponentArgumentType implements ArgumentType<Component> {
    private static final GsonComponentSerializer GSON_SERIALIER = GsonComponentSerializer.gson();
    public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION =
            new DynamicCommandExceptionType(x -> new ComponentMessage(Component.translatable("argument.component.invalid")
                    .arguments(Component.text(x.toString()))));

    private static final Field JSON_READER_POS_FIELD;
    private static final Field JSON_READER_LINESTART_FIELD;

    static {
        try {
            JSON_READER_POS_FIELD = JsonReader.class.getDeclaredField("pos");
            JSON_READER_POS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Couldn't get 'pos' field for JsonReader", e);
        }

        try {
            JSON_READER_LINESTART_FIELD = JsonReader.class.getDeclaredField("lineStart");
            JSON_READER_LINESTART_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Couldn't get 'pos' field for JsonReader", e);
        }
    }

    @NotNull
    public static ComponentArgumentType component() {
        return new ComponentArgumentType();
    }

    @Override
    public Component parse(StringReader reader) throws CommandSyntaxException {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(reader.getRemaining()));
        jsonReader.setLenient(false);

        try {
            try {
                JsonElement jsonElement = Streams.parse(jsonReader);
                return GSON_SERIALIER.serializer().fromJson(jsonElement, Component.class);
            } finally {
                reader.setCursor(reader.getCursor() + JSON_READER_POS_FIELD.getInt(jsonReader) - JSON_READER_LINESTART_FIELD.getInt(jsonReader));
            }
        } catch (Exception e) {
            throw INVALID_COMPONENT_EXCEPTION.createWithContext(reader, e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }
    }
}
