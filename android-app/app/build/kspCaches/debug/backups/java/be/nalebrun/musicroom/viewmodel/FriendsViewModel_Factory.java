package be.nalebrun.musicroom.viewmodel;

import be.nalebrun.musicroom.APIRepository;
import be.nalebrun.musicroom.repositories.CredentialRepository;
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
public final class FriendsViewModel_Factory implements Factory<FriendsViewModel> {
  private final Provider<APIRepository> apiRepositoryProvider;

  private final Provider<CredentialRepository> credentialRepositoryProvider;

  private final Provider<ISettingsRepository> settingsRepositoryProvider;

  private FriendsViewModel_Factory(Provider<APIRepository> apiRepositoryProvider,
      Provider<CredentialRepository> credentialRepositoryProvider,
      Provider<ISettingsRepository> settingsRepositoryProvider) {
    this.apiRepositoryProvider = apiRepositoryProvider;
    this.credentialRepositoryProvider = credentialRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public FriendsViewModel get() {
    return newInstance(apiRepositoryProvider.get(), credentialRepositoryProvider.get(), settingsRepositoryProvider.get());
  }

  public static FriendsViewModel_Factory create(Provider<APIRepository> apiRepositoryProvider,
      Provider<CredentialRepository> credentialRepositoryProvider,
      Provider<ISettingsRepository> settingsRepositoryProvider) {
    return new FriendsViewModel_Factory(apiRepositoryProvider, credentialRepositoryProvider, settingsRepositoryProvider);
  }

  public static FriendsViewModel newInstance(APIRepository apiRepository,
      CredentialRepository credentialRepository, ISettingsRepository settingsRepository) {
    return new FriendsViewModel(apiRepository, credentialRepository, settingsRepository);
  }
}
