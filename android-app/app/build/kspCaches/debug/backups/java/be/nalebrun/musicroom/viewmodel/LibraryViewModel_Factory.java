package be.nalebrun.musicroom.viewmodel;

import be.nalebrun.musicroom.IAPIRepository;
import be.nalebrun.musicroom.repositories.ICredentialRepository;
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
public final class LibraryViewModel_Factory implements Factory<LibraryViewModel> {
  private final Provider<IAPIRepository> apiRepositoryProvider;

  private final Provider<ICredentialRepository> credentialRepositoryProvider;

  private LibraryViewModel_Factory(Provider<IAPIRepository> apiRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider) {
    this.apiRepositoryProvider = apiRepositoryProvider;
    this.credentialRepositoryProvider = credentialRepositoryProvider;
  }

  @Override
  public LibraryViewModel get() {
    return newInstance(apiRepositoryProvider.get(), credentialRepositoryProvider.get());
  }

  public static LibraryViewModel_Factory create(Provider<IAPIRepository> apiRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider) {
    return new LibraryViewModel_Factory(apiRepositoryProvider, credentialRepositoryProvider);
  }

  public static LibraryViewModel newInstance(IAPIRepository apiRepository,
      ICredentialRepository credentialRepository) {
    return new LibraryViewModel(apiRepository, credentialRepository);
  }
}
