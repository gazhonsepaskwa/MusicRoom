package be.nalebrun.musicroom.viewmodel;

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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<ISettingsRepository> settingsRepositoryProvider;

  private final Provider<ICredentialRepository> credentialRepositoryProvider;

  private SettingsViewModel_Factory(Provider<ISettingsRepository> settingsRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.credentialRepositoryProvider = credentialRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(settingsRepositoryProvider.get(), credentialRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<ISettingsRepository> settingsRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider) {
    return new SettingsViewModel_Factory(settingsRepositoryProvider, credentialRepositoryProvider);
  }

  public static SettingsViewModel newInstance(ISettingsRepository settingsRepository,
      ICredentialRepository credentialRepository) {
    return new SettingsViewModel(settingsRepository, credentialRepository);
  }
}
