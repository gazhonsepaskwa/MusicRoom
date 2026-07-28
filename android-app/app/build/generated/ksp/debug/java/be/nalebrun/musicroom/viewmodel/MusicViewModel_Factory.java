package be.nalebrun.musicroom.viewmodel;

import be.nalebrun.musicroom.repositories.MusicRepository;
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
public final class MusicViewModel_Factory implements Factory<MusicViewModel> {
  private final Provider<MusicRepository> musicRepositoryProvider;

  private MusicViewModel_Factory(Provider<MusicRepository> musicRepositoryProvider) {
    this.musicRepositoryProvider = musicRepositoryProvider;
  }

  @Override
  public MusicViewModel get() {
    return newInstance(musicRepositoryProvider.get());
  }

  public static MusicViewModel_Factory create(Provider<MusicRepository> musicRepositoryProvider) {
    return new MusicViewModel_Factory(musicRepositoryProvider);
  }

  public static MusicViewModel newInstance(MusicRepository musicRepository) {
    return new MusicViewModel(musicRepository);
  }
}
