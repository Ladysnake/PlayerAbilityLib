package io.github.ladysnake.pal.impl;

import com.google.common.base.Preconditions;
import net.minecraft.util.Identifier;

import java.util.Objects;

public abstract class TypedIdentifier {
    protected final Identifier id;

    public TypedIdentifier(Identifier id) {
        Preconditions.checkNotNull(id);
        this.id = id;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypedIdentifier that = (TypedIdentifier) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
