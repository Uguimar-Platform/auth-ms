/*
https://docs.nestjs.com/modules
*/

import { Module } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import { CreateUserUseCase } from './use-cases/create-user.usecase';
import { PrismaUserRepositoryAdapter } from 'src/infrastructure/repositories/prisma.user.repository.adapter';

@Module({
    imports: [],
    controllers: [],
    providers: [
        {
            provide: PrismaClient,
            useValue: new PrismaClient(),
        },
        CreateUserUseCase,
        {
            provide: 'UserRepository',
            useClass: PrismaUserRepositoryAdapter,
        }
    ],
    exports: [
        CreateUserUseCase
    ]
})
export class ApplicationModule {}
