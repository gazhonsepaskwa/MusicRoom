package be.nalebrun.musicroom.viewmodel;

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
public final class NavigationViewModel_Factory implements Factory<NavigationViewModel> {
  private final Provider<UiMessageManager> uiMessageManagerProvider;

  private NavigationViewModel_Factory(Provider<UiMessageManager> uiMessageManagerProvider) {
    this.uiMessageManagerProvider = uiMessageManagerProvider;
  }

  @Override
  public NavigationViewModel get() {
    return newInstance(uiMessageManagerProvider.get());
  }

  public static NavigationViewModel_Factory create(
      Provider<UiMessageManager> uiMessageManagerProvider) {
    return new NavigationViewModel_Factory(uiMessageManagerProvider);
  }

  public static NavigationViewModel newInstance(UiMessageManager uiMessageManager) {
    return new NavigationViewModel(uiMessageManager);
  }
}
