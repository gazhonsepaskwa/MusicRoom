package be.nalebrun.musicroom;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import be.nalebrun.musicroom.di.NetworkModule_ProvideOkHttpClientFactory;
import be.nalebrun.musicroom.repositories.CredentialRepository;
import be.nalebrun.musicroom.repositories.CredentialRepository_Factory;
import be.nalebrun.musicroom.repositories.ICredentialRepository;
import be.nalebrun.musicroom.repositories.ISettingsRepository;
import be.nalebrun.musicroom.repositories.MusicRepository;
import be.nalebrun.musicroom.repositories.MusicRepository_Factory;
import be.nalebrun.musicroom.repositories.SettingsRepository;
import be.nalebrun.musicroom.repositories.SettingsRepository_Factory;
import be.nalebrun.musicroom.services.PlaybackService;
import be.nalebrun.musicroom.services.PlaybackService_MembersInjector;
import be.nalebrun.musicroom.viewmodel.AuthViewModel;
import be.nalebrun.musicroom.viewmodel.AuthViewModel_Factory;
import be.nalebrun.musicroom.viewmodel.AuthViewModel_HiltModules;
import be.nalebrun.musicroom.viewmodel.AuthViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.AuthViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.FriendsViewModel;
import be.nalebrun.musicroom.viewmodel.FriendsViewModel_Factory;
import be.nalebrun.musicroom.viewmodel.FriendsViewModel_HiltModules;
import be.nalebrun.musicroom.viewmodel.FriendsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.FriendsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.MusicViewModel;
import be.nalebrun.musicroom.viewmodel.MusicViewModel_Factory;
import be.nalebrun.musicroom.viewmodel.MusicViewModel_HiltModules;
import be.nalebrun.musicroom.viewmodel.MusicViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.MusicViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.NavigationViewModel_Factory;
import be.nalebrun.musicroom.viewmodel.NavigationViewModel_HiltModules;
import be.nalebrun.musicroom.viewmodel.NavigationViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.NavigationViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.SearchViewModel;
import be.nalebrun.musicroom.viewmodel.SearchViewModel_Factory;
import be.nalebrun.musicroom.viewmodel.SearchViewModel_HiltModules;
import be.nalebrun.musicroom.viewmodel.SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.SettingsViewModel;
import be.nalebrun.musicroom.viewmodel.SettingsViewModel_Factory;
import be.nalebrun.musicroom.viewmodel.SettingsViewModel_HiltModules;
import be.nalebrun.musicroom.viewmodel.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.UserProfileViewModel;
import be.nalebrun.musicroom.viewmodel.UserProfileViewModel_Factory;
import be.nalebrun.musicroom.viewmodel.UserProfileViewModel_HiltModules;
import be.nalebrun.musicroom.viewmodel.UserProfileViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import be.nalebrun.musicroom.viewmodel.UserProfileViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class DaggerMusicRoomApp_HiltComponents_SingletonC {
  private DaggerMusicRoomApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MusicRoomApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MusicRoomApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MusicRoomApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MusicRoomApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MusicRoomApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MusicRoomApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MusicRoomApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MusicRoomApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MusicRoomApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MusicRoomApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MusicRoomApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MusicRoomApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MusicRoomApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MusicRoomApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MusicRoomApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MusicRoomApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MusicRoomApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends MusicRoomApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MusicRoomApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    ImmutableMap keySetMapOfClassOfObjectAndBooleanBuilder() {
      ImmutableMap.Builder mapBuilder = ImmutableMap.<String, Boolean>builderWithExpectedSize(7);
      mapBuilder.put(AuthViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AuthViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(FriendsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, FriendsViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(MusicViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, MusicViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(NavigationViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, NavigationViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SearchViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(UserProfileViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, UserProfileViewModel_HiltModules.KeyModule.provide());
      return mapBuilder.build();
    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(keySetMapOfClassOfObjectAndBooleanBuilder());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends MusicRoomApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<AuthViewModel> authViewModelProvider;

    Provider<FriendsViewModel> friendsViewModelProvider;

    Provider<MusicViewModel> musicViewModelProvider;

    Provider<SearchViewModel> searchViewModelProvider;

    Provider<SettingsViewModel> settingsViewModelProvider;

    Provider<UserProfileViewModel> userProfileViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    ImmutableMap hiltViewModelMapMapOfClassOfObjectAndProviderOfViewModelBuilder() {
      ImmutableMap.Builder mapBuilder = ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(7);
      mapBuilder.put(AuthViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (authViewModelProvider)));
      mapBuilder.put(FriendsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (friendsViewModelProvider)));
      mapBuilder.put(MusicViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (musicViewModelProvider)));
      mapBuilder.put(NavigationViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (NavigationViewModel_Factory.create())));
      mapBuilder.put(SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (searchViewModelProvider)));
      mapBuilder.put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (settingsViewModelProvider)));
      mapBuilder.put(UserProfileViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (userProfileViewModelProvider)));
      return mapBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.authViewModelProvider = AuthViewModel_Factory.create(singletonCImpl.bindAPIRepositoryProvider, singletonCImpl.bindCredentialRepositoryProvider, singletonCImpl.bindSettingsRepositoryProvider);
      this.friendsViewModelProvider = FriendsViewModel_Factory.create(singletonCImpl.aPIRepositoryProvider, singletonCImpl.credentialRepositoryProvider, singletonCImpl.bindSettingsRepositoryProvider);
      this.musicViewModelProvider = MusicViewModel_Factory.create(singletonCImpl.musicRepositoryProvider);
      this.searchViewModelProvider = SearchViewModel_Factory.create(singletonCImpl.bindAPIRepositoryProvider, singletonCImpl.bindCredentialRepositoryProvider);
      this.settingsViewModelProvider = SettingsViewModel_Factory.create(singletonCImpl.bindSettingsRepositoryProvider, singletonCImpl.bindCredentialRepositoryProvider);
      this.userProfileViewModelProvider = UserProfileViewModel_Factory.create(singletonCImpl.bindAPIRepositoryProvider, singletonCImpl.bindCredentialRepositoryProvider);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(hiltViewModelMapMapOfClassOfObjectAndProviderOfViewModelBuilder());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }
  }

  private static final class ActivityRetainedCImpl extends MusicRoomApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.create());
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }
  }

  private static final class ServiceCImpl extends MusicRoomApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectPlaybackService(PlaybackService arg0) {
      injectPlaybackService2(arg0);
    }

    @CanIgnoreReturnValue
    private PlaybackService injectPlaybackService2(PlaybackService instance) {
      PlaybackService_MembersInjector.injectMusicRepository(instance, singletonCImpl.musicRepositoryProvider.get());
      PlaybackService_MembersInjector.injectCredentialRepository(instance, singletonCImpl.bindCredentialRepositoryProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends MusicRoomApp_HiltComponents.SingletonC {
    private final SingletonCImpl singletonCImpl = this;

    Provider<Context> provideContextProvider;

    Provider<SettingsRepository> settingsRepositoryProvider;

    Provider<ISettingsRepository> bindSettingsRepositoryProvider;

    Provider<OkHttpClient> provideOkHttpClientProvider;

    Provider<APIRepository> aPIRepositoryProvider;

    Provider<IAPIRepository> bindAPIRepositoryProvider;

    Provider<CredentialRepository> credentialRepositoryProvider;

    Provider<ICredentialRepository> bindCredentialRepositoryProvider;

    Provider<MusicRepository> musicRepositoryProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {

      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideContextProvider = ApplicationContextModule_ProvideContextFactory.create(applicationContextModuleParam);
      this.settingsRepositoryProvider = SettingsRepository_Factory.create(provideContextProvider);
      this.bindSettingsRepositoryProvider = DoubleCheck.provider((Provider) (settingsRepositoryProvider));
      this.provideOkHttpClientProvider = DoubleCheck.provider(NetworkModule_ProvideOkHttpClientFactory.create());
      this.aPIRepositoryProvider = APIRepository_Factory.create(provideOkHttpClientProvider, bindSettingsRepositoryProvider);
      this.bindAPIRepositoryProvider = DoubleCheck.provider((Provider) (aPIRepositoryProvider));
      this.credentialRepositoryProvider = CredentialRepository_Factory.create(provideContextProvider);
      this.bindCredentialRepositoryProvider = DoubleCheck.provider((Provider) (credentialRepositoryProvider));
      this.musicRepositoryProvider = DoubleCheck.provider(MusicRepository_Factory.create(provideContextProvider, aPIRepositoryProvider, credentialRepositoryProvider));
    }

    @Override
    public void injectMusicRoomApp(MusicRoomApp arg0) {
      injectMusicRoomApp2(arg0);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private MusicRoomApp injectMusicRoomApp2(MusicRoomApp instance) {
      MusicRoomApp_MembersInjector.injectSettingsRepository(instance, bindSettingsRepositoryProvider.get());
      return instance;
    }
  }
}
