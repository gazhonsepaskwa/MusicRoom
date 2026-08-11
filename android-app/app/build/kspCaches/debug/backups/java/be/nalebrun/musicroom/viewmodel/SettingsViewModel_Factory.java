package be.nalebrun.musicroom.viewmodel;

import be.nalebrun.musicroom.IAPIRepository;
import be.nalebrun.musicroom.repositories.ICredentialRepository;
import be.nalebrun.musicroom.repositories.ISettingsRepository;
import be.nalebrun.musicroom.repositories.UiMessageManager;
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

  private final Provider<IAPIRepository> apiRepositoryProvider;

  private final Provider<UiMessageManager> uiMessageManagerProvider;

  private SettingsViewModel_Factory(Provider<ISettingsRepository> settingsRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider,
      Provider<IAPIRepository> apiRepositoryProvider,
      Provider<UiMessageManager> uiMessageManagerProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.credentialRepositoryProvider = credentialRepositoryProvider;
    this.apiRepositoryProvider = apiRepositoryProvider;
    this.uiMessageManagerProvider = uiMessageManagerProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(settingsRepositoryProvider.get(), credentialRepositoryProvider.get(), apiRepositoryProvider.get(), uiMessageManagerProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<ISettingsRepository> settingsRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider,
      Provider<IAPIRepository> apiRepositoryProvider,
      Provider<UiMessageManager> uiMessageManagerProvider) {
    return new SettingsViewModel_Factory(settingsRepositoryProvider, credentialRepositoryProvider, apiRepositoryProvider, uiMessageManagerProvider);
  }

  public static SettingsViewModel newInstance(ISettingsRepository settingsRepository,
      ICredentialRepository credentialRepository, IAPIRepository apiRepository,
      UiMessageManager uiMessageManager) {
    return new SettingsViewModel(settingsRepository, credentialRepository, apiRepository, uiMessageManager);
  }
}
