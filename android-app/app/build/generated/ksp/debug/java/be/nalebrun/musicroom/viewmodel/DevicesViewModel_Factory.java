package be.nalebrun.musicroom.viewmodel;

import be.nalebrun.musicroom.APIRepository;
import be.nalebrun.musicroom.repositories.CredentialRepository;
import be.nalebrun.musicroom.repositories.SocketIORepository;
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
public final class DevicesViewModel_Factory implements Factory<DevicesViewModel> {
  private final Provider<CredentialRepository> credentialRepositoryProvider;

  private final Provider<APIRepository> apiRepositoryProvider;

  private final Provider<SocketIORepository> socketIORepositoryProvider;

  private DevicesViewModel_Factory(Provider<CredentialRepository> credentialRepositoryProvider,
      Provider<APIRepository> apiRepositoryProvider,
      Provider<SocketIORepository> socketIORepositoryProvider) {
    this.credentialRepositoryProvider = credentialRepositoryProvider;
    this.apiRepositoryProvider = apiRepositoryProvider;
    this.socketIORepositoryProvider = socketIORepositoryProvider;
  }

  @Override
  public DevicesViewModel get() {
    return newInstance(credentialRepositoryProvider.get(), apiRepositoryProvider.get(), socketIORepositoryProvider.get());
  }

  public static DevicesViewModel_Factory create(
      Provider<CredentialRepository> credentialRepositoryProvider,
      Provider<APIRepository> apiRepositoryProvider,
      Provider<SocketIORepository> socketIORepositoryProvider) {
    return new DevicesViewModel_Factory(credentialRepositoryProvider, apiRepositoryProvider, socketIORepositoryProvider);
  }

  public static DevicesViewModel newInstance(CredentialRepository credentialRepository,
      APIRepository apiRepository, SocketIORepository socketIORepository) {
    return new DevicesViewModel(credentialRepository, apiRepository, socketIORepository);
  }
}
