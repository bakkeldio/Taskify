package nau.android.taskify.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nau.android.taskify.data.dataSource.CategoriesLocalDataSource
import nau.android.taskify.data.dataSource.ICategoriesLocalDataSource
import nau.android.taskify.data.repository.CategoryRepository
import nau.android.taskify.data.repository.ICategoryRepository
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
abstract class CategoryModule {


    @Singleton
    @Binds
    abstract fun bindCategoryRepo(categoryRepository: CategoryRepository): ICategoryRepository

    @Singleton
    @Binds
    abstract fun bindCategoryLocalDataSource(categoryDataSource: CategoriesLocalDataSource): ICategoriesLocalDataSource
}