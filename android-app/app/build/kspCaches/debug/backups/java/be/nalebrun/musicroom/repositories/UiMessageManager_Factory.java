package be.nalebrun.musicroom.repositories;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class UiMessageManager_Factory implements Factory<UiMessageManager> {
  @Override
  public UiMessageManager get() {
    return newInstance();
  }

  public static UiMessageManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UiMessageManager newInstance() {
    return new UiMessageManager();
  }

  private static final class InstanceHolder {
    static final UiMessageManager_Factory INSTANCE = new UiMessageManager_Factory();
  }
}
