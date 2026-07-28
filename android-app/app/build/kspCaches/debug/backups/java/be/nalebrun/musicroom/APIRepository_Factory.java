package be.nalebrun.musicroom;

import be.nalebrun.musicroom.repositories.ISettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class APIRepository_Factory implements Factory<APIRepository> {
  private final Provider<OkHttpClient> clientProvider;

  private final Provider<ISettingsRepository> settingsRepositoryProvider;

  private APIRepository_Factory(Provider<OkHttpClient> clientProvider,
      Provider<ISettingsRepository> settingsRepositoryProvider) {
    this.clientProvider = clientProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public APIRepository get() {
    return newInstance(clientProvider.get(), settingsRepositoryProvider.get());
  }

  public static APIRepository_Factory create(Provider<OkHttpClient> clientProvider,
      Provider<ISettingsRepository> settingsRepositoryProvider) {
    return new APIRepository_Factory(clientProvider, settingsRepositoryProvider);
  }

  public static APIRepository newInstance(OkHttpClient client,
      ISettingsRepository settingsRepository) {
    return new APIRepository(client, settingsRepository);
  }
}
