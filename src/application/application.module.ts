/*
https://docs.nestjs.com/modules
*/

import { Module } from '@nestjs/common';
import { PrismaUserRepositoryAdapter } from 'src/infrastructure/repositories/prisma.user.repository.adapter';
import { PrismaClient } from '@prisma/client';
import { CreateRoleUseCase } from './use-cases/roles/create-role.usecase';
import { PrismaPermissionRepositoryAdapter } from '../infrastructure/repositories/prisma.permission.repository.adapter';
import { CreatePermissionsUseCase } from './use-cases/permissions/create-permissions';
import { CreateModuleUseCase } from './use-cases/modules/create-module.usecase';
import { RegisterUserUseCase } from './use-cases/users/register-user.usecase';
import { PrismaModuleRepositoryAdapter } from 'src/infrastructure/repositories/prisma.module.repository.adapter';
import { FindModuleByIdUseCase } from './use-cases/modules/find-module-by-id.usecase';
/**
 * Module that groups all the application dependencies.
 */
@Module({
  imports: [],
  providers: [
    {
      provide: PrismaClient,
      useValue: new PrismaClient(),
    },
    RegisterUserUseCase,
    {
      provide: 'UserRepository',
      useClass: PrismaUserRepositoryAdapter,
    },
    CreateRoleUseCase,
    {
      provide: 'RoleRepository',
      useClass: PrismaUserRepositoryAdapter,
    },
    CreatePermissionsUseCase,
    {
      provide: 'PermissionRepository',
      useClass: PrismaPermissionRepositoryAdapter,
    },
    CreateModuleUseCase,
    FindModuleByIdUseCase,
    {
      provide: 'ModuleRepository',
      useClass: PrismaModuleRepositoryAdapter,
    },
  ],
  exports: [RegisterUserUseCase, CreateRoleUseCase, CreatePermissionsUseCase, CreateModuleUseCase, FindModuleByIdUseCase ],
})
export class ApplicationModule {}
