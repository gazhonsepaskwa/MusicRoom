package be.nalebrun.musicroom.repositories;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class CredentialRepository_Factory implements Factory<CredentialRepository> {
  private final Provider<Context> contextProvider;

  private CredentialRepository_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CredentialRepository get() {
    return newInstance(contextProvider.get());
  }

  public static CredentialRepository_Factory create(Provider<Context> contextProvider) {
    return new CredentialRepository_Factory(contextProvider);
  }

  public static CredentialRepository newInstance(Context context) {
    return new CredentialRepository(context);
  }
}
