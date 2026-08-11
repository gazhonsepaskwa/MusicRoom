package be.nalebrun.musicroom.repositories;

import android.content.Context;
import be.nalebrun.musicroom.APIRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class MusicRepository_Factory implements Factory<MusicRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<APIRepository> apiRepositoryProvider;

  private final Provider<CredentialRepository> credentialRepositoryProvider;

  private final Provider<SocketIORepository> socketIORepositoryProvider;

  private MusicRepository_Factory(Provider<Context> contextProvider,
      Provider<APIRepository> apiRepositoryProvider,
      Provider<CredentialRepository> credentialRepositoryProvider,
      Provider<SocketIORepository> socketIORepositoryProvider) {
    this.contextProvider = contextProvider;
    this.apiRepositoryProvider = apiRepositoryProvider;
    this.credentialRepositoryProvider = credentialRepositoryProvider;
    this.socketIORepositoryProvider = socketIORepositoryProvider;
  }

  @Override
  public MusicRepository get() {
    return newInstance(contextProvider.get(), apiRepositoryProvider.get(), credentialRepositoryProvider.get(), socketIORepositoryProvider.get());
  }

  public static MusicRepository_Factory create(Provider<Context> contextProvider,
      Provider<APIRepository> apiRepositoryProvider,
      Provider<CredentialRepository> credentialRepositoryProvider,
      Provider<SocketIORepository> socketIORepositoryProvider) {
    return new MusicRepository_Factory(contextProvider, apiRepositoryProvider, credentialRepositoryProvider, socketIORepositoryProvider);
  }

  public static MusicRepository newInstance(Context context, APIRepository apiRepository,
      CredentialRepository credentialRepository, SocketIORepository socketIORepository) {
    return new MusicRepository(context, apiRepository, credentialRepository, socketIORepository);
  }
}
