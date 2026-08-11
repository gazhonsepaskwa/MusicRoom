package be.nalebrun.musicroom.viewmodel;

import be.nalebrun.musicroom.repositories.IMusicRepository;
import be.nalebrun.musicroom.repositories.ISettingsRepository;
import be.nalebrun.musicroom.repositories.ISocketIORepository;
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
public final class SocketViewModel_Factory implements Factory<SocketViewModel> {
  private final Provider<ISocketIORepository> socketIORepositoryProvider;

  private final Provider<IMusicRepository> musicRepositoryProvider;

  private final Provider<ISettingsRepository> settingsRepositoryProvider;

  private final Provider<UiMessageManager> uiMessageManagerProvider;

  private SocketViewModel_Factory(Provider<ISocketIORepository> socketIORepositoryProvider,
      Provider<IMusicRepository> musicRepositoryProvider,
      Provider<ISettingsRepository> settingsRepositoryProvider,
      Provider<UiMessageManager> uiMessageManagerProvider) {
    this.socketIORepositoryProvider = socketIORepositoryProvider;
    this.musicRepositoryProvider = musicRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.uiMessageManagerProvider = uiMessageManagerProvider;
  }

  @Override
  public SocketViewModel get() {
    return newInstance(socketIORepositoryProvider.get(), musicRepositoryProvider.get(), settingsRepositoryProvider.get(), uiMessageManagerProvider.get());
  }

  public static SocketViewModel_Factory create(
      Provider<ISocketIORepository> socketIORepositoryProvider,
      Provider<IMusicRepository> musicRepositoryProvider,
      Provider<ISettingsRepository> settingsRepositoryProvider,
      Provider<UiMessageManager> uiMessageManagerProvider) {
    return new SocketViewModel_Factory(socketIORepositoryProvider, musicRepositoryProvider, settingsRepositoryProvider, uiMessageManagerProvider);
  }

  public static SocketViewModel newInstance(ISocketIORepository socketIORepository,
      IMusicRepository musicRepository, ISettingsRepository settingsRepository,
      UiMessageManager uiMessageManager) {
    return new SocketViewModel(socketIORepository, musicRepository, settingsRepository, uiMessageManager);
  }
}
