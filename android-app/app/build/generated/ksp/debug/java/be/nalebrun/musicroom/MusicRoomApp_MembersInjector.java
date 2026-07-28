package be.nalebrun.musicroom;

import be.nalebrun.musicroom.repositories.ISettingsRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class MusicRoomApp_MembersInjector implements MembersInjector<MusicRoomApp> {
  private final Provider<ISettingsRepository> settingsRepositoryProvider;

  private MusicRoomApp_MembersInjector(Provider<ISettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public void injectMembers(MusicRoomApp instance) {
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
  }

  public static MembersInjector<MusicRoomApp> create(
      Provider<ISettingsRepository> settingsRepositoryProvider) {
    return new MusicRoomApp_MembersInjector(settingsRepositoryProvider);
  }

  @InjectedFieldSignature("be.nalebrun.musicroom.MusicRoomApp.settingsRepository")
  public static void injectSettingsRepository(MusicRoomApp instance,
      ISettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }
}
