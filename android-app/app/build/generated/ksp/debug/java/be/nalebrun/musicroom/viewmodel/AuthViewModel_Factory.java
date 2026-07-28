package be.nalebrun.musicroom.viewmodel;

import be.nalebrun.musicroom.IAPIRepository;
import be.nalebrun.musicroom.repositories.ICredentialRepository;
import be.nalebrun.musicroom.repositories.ISettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<IAPIRepository> apiRepositoryProvider;

  private final Provider<ICredentialRepository> credentialRepositoryProvider;

  private final Provider<ISettingsRepository> settingsRepositoryProvider;

  private AuthViewModel_Factory(Provider<IAPIRepository> apiRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider,
      Provider<ISettingsRepository> settingsRepositoryProvider) {
    this.apiRepositoryProvider = apiRepositoryProvider;
    this.credentialRepositoryProvider = credentialRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(apiRepositoryProvider.get(), credentialRepositoryProvider.get(), settingsRepositoryProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<IAPIRepository> apiRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider,
      Provider<ISettingsRepository> settingsRepositoryProvider) {
    return new AuthViewModel_Factory(apiRepositoryProvider, credentialRepositoryProvider, settingsRepositoryProvider);
  }

  public static AuthViewModel newInstance(IAPIRepository apiRepository,
      ICredentialRepository credentialRepository, ISettingsRepository settingsRepository) {
    return new AuthViewModel(apiRepository, credentialRepository, settingsRepository);
  }
}
