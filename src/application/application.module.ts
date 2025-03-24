/*
https://docs.nestjs.com/modules
*/

import { Module } from '@nestjs/common';
import { PrismaUserRepositoryAdapter } from 'src/infrastructure/repositories/prisma.user.repository.adapter';
import { PrismaClient } from '@prisma/client';
import { CreateRoleUseCase } from './use-cases/roles/create-role.usecase';
import { PrismaPermissionRepositoryAdapter } from '../infrastructure/repositories/prisma.permission.repository.adapter';
import { CreatedPermissionsUseCase } from './use-cases/permissions/create-permissions.usecase';
import { CreateModuleUseCase } from './use-cases/modules/create-module.usecase';
import { RegisterUserUseCase } from './use-cases/users/register-user.usecase';
import { PrismaModuleRepositoryAdapter } from 'src/infrastructure/repositories/prisma.module.repository.adapter';
import { DomainModule } from 'src/domain/domain.module';
import { LoginUserUseCase } from './use-cases/users/login.usecase';
import { FindModuleByIdUseCase } from './use-cases/modules/find-module-by-id.usecase';
import { UpdateModuleUseCase } from './use-cases/modules/update-module.usecase';
import { UpdateStatusModuleUseCase } from './use-cases/modules/update-status.usecase';
import { ListModulePaginatedUseCase } from './use-cases/modules/list-module-paginated.usecase';
import { FindRoleByIdUseCase } from './use-cases/roles/find-role-by-id.usecase';
import { ListRolesPaginatedUseCase } from './use-cases/roles/list-roles-paginated.usecase';
import { UpdateRoleUseCase } from './use-cases/roles/update-role.usecase';
import { UpdateStatusRoleUseCase } from './use-cases/roles/update-status.usecase';
import { PrismaRoleRepositoryAdapter } from 'src/infrastructure/repositories/prisma.role.repository.adapter';
import { ListPermissionsPaginatedUseCase } from './use-cases/permissions/list-permissions-paginated.usecase';
import { UpdatePermissionUseCase } from './use-cases/permissions/update-permission.usecase';
import { UpdateUserUseCase } from './use-cases/users/update-user.usecase';
import { UpdateStatusUserUseCase } from './use-cases/users/update-status.usecase';
import { ListUserPaginatedUseCase } from './use-cases/users/list-user.usecase';
/**
 * Module that groups all the application dependencies.
 */
@Module({
  imports: [DomainModule],
  providers: [
    {
      provide: PrismaClient,
      useValue: new PrismaClient(),
    },
    RegisterUserUseCase,
    UpdateUserUseCase,
    UpdateStatusUserUseCase,
    ListUserPaginatedUseCase,
    {
      provide: 'UserRepository',
      useClass: PrismaUserRepositoryAdapter,
    },
    CreateRoleUseCase,
    FindRoleByIdUseCase,
    ListRolesPaginatedUseCase,
    UpdateRoleUseCase,
    UpdateStatusRoleUseCase,
    {
      provide: 'RoleRepository',
      useClass: PrismaRoleRepositoryAdapter,
    },
    CreatedPermissionsUseCase,
    ListPermissionsPaginatedUseCase,
    UpdatePermissionUseCase,
    {
      provide: 'PermissionRepository',
      useClass: PrismaPermissionRepositoryAdapter,
    },
    CreateModuleUseCase,
    LoginUserUseCase,
    FindModuleByIdUseCase,
    UpdateModuleUseCase,
    UpdateStatusModuleUseCase,
    ListModulePaginatedUseCase,
    {
      provide: 'ModuleRepository',
      useClass: PrismaModuleRepositoryAdapter,
    },
  ],
  exports: [
    RegisterUserUseCase,
    UpdateUserUseCase,
    LoginUserUseCase,
    UpdateStatusUserUseCase,
    ListUserPaginatedUseCase,
    CreateRoleUseCase,
    CreatedPermissionsUseCase,
    ListPermissionsPaginatedUseCase,
    UpdatePermissionUseCase,
    CreateModuleUseCase,
    FindModuleByIdUseCase,
    UpdateModuleUseCase,
    UpdateStatusModuleUseCase,
    ListModulePaginatedUseCase,
    CreateRoleUseCase,
    FindRoleByIdUseCase,
    ListRolesPaginatedUseCase,
    UpdateRoleUseCase,
    UpdateStatusRoleUseCase,
  ],
})
export class ApplicationModule {}
