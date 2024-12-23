package com.noemi.tracker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.noemi.tracker.manager.DataManager
import com.noemi.tracker.manager.DataManagerImpl
import com.noemi.tracker.repository.AuthRepository
import com.noemi.tracker.repository.AuthRepositoryImpl
import com.noemi.tracker.providers.DispatcherProvider
import com.noemi.tracker.providers.DispatcherSourceProvider
import com.noemi.tracker.repository.ExpensesRepository
import com.noemi.tracker.repository.ExpensesRepositoryImpl
import com.noemi.tracker.room.ExpenseDAO
import com.noemi.tracker.room.ExpensesDatabase
import com.noemi.tracker.usecase.CurrentMonthExpensesUseCase
import com.noemi.tracker.usecase.SelectedPeriodExpensesUseCase
import com.noemi.tracker.utils.EXPENSE_DB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository = AuthRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun providesDispatchers(): DispatcherProvider = DispatcherSourceProvider

    @Provides
    @Singleton
    fun providesSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun providesDataManager(sharedPreferences: SharedPreferences): DataManager = DataManagerImpl(sharedPreferences)

    @Provides
    @Singleton
    fun providesExpensesDatabase(@ApplicationContext context: Context): ExpensesDatabase =
        Room.databaseBuilder(context, ExpensesDatabase::class.java, EXPENSE_DB)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providesExpensesDao(expensesDatabase: ExpensesDatabase): ExpenseDAO = expensesDatabase.getExpenseDAO()

    @Provides
    @Singleton
    fun providesExpensesRepository(
        expenseDAO: ExpenseDAO,
        dispatcherProvider: DispatcherProvider
    ): ExpensesRepository =
        ExpensesRepositoryImpl(
            expenseDAO = expenseDAO,
            dispatcherProvider = dispatcherProvider
        )

    @Provides
    @Singleton
    fun providesCurrentMonthExpensesUseCase(expensesRepository: ExpensesRepository) = CurrentMonthExpensesUseCase(expensesRepository)

    @Provides
    @Singleton
    fun providesSelectedPeriodExpensesUseCase(expensesRepository: ExpensesRepository) = SelectedPeriodExpensesUseCase(expensesRepository)
}