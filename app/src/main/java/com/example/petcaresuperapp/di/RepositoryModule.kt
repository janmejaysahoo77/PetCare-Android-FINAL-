package com.example.petcaresuperapp.di

import com.example.petcaresuperapp.data.repository.AdoptionRepositoryImpl
import com.example.petcaresuperapp.data.repository.PetRepositoryImpl
import com.example.petcaresuperapp.domain.repository.AdoptionRepository
import com.example.petcaresuperapp.domain.repository.PetRepository
import com.example.petcaresuperapp.domain.repository.VetAppointmentRepository
import com.example.petcaresuperapp.domain.repository.MedicalRecordRepository
import com.example.petcaresuperapp.data.repository.VetAppointmentRepositoryImpl
import com.example.petcaresuperapp.data.repository.MedicalRecordRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPetRepository(
        petRepositoryImpl: PetRepositoryImpl
    ): PetRepository

    @Binds
    @Singleton
    abstract fun bindAdoptionRepository(
        adoptionRepositoryImpl: AdoptionRepositoryImpl
    ): AdoptionRepository

    @Binds
    @Singleton
    abstract fun bindVetAppointmentRepository(
        vetAppointmentRepositoryImpl: VetAppointmentRepositoryImpl
    ): VetAppointmentRepository

    @Binds
    @Singleton
    abstract fun bindMedicalRecordRepository(
        medicalRecordRepositoryImpl: MedicalRecordRepositoryImpl
    ): MedicalRecordRepository
}
