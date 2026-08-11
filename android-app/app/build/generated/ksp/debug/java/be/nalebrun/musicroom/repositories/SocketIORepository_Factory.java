package be.nalebrun.musicroom.repositories;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
public final class SocketIORepository_Factory implements Factory<SocketIORepository> {
  private final Provider<ISettingsRepository> settingsRepositoryProvider;

  private final Provider<ICredentialRepository> credentialRepositoryProvider;

  private SocketIORepository_Factory(Provider<ISettingsRepository> settingsRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.credentialRepositoryProvider = credentialRepositoryProvider;
  }

  @Override
  public SocketIORepository get() {
    return newInstance(settingsRepositoryProvider.get(), credentialRepositoryProvider.get());
  }

  public static SocketIORepository_Factory create(
      Provider<ISettingsRepository> settingsRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider) {
    return new SocketIORepository_Factory(settingsRepositoryProvider, credentialRepositoryProvider);
  }

  public static SocketIORepository newInstance(ISettingsRepository settingsRepository,
      ICredentialRepository credentialRepository) {
    return new SocketIORepository(settingsRepository, credentialRepository);
  }
}
