/*
https://docs.nestjs.com/modules
*/

import { Module } from '@nestjs/common';
import { ApplicationModule } from 'src/application/application.module';
import { UserController } from './controllers/user.controller';
import { ModuleController } from './controllers/module.controller';
import { DomainModule } from 'src/domain/domain.module';
import { RolesController } from './controllers/roles.controller';
import { PermissionController } from './controllers/permission.controller';

@Module({
  imports: [ApplicationModule, DomainModule],
  controllers: [UserController, ModuleController, RolesController, PermissionController],
  providers: [],
})
export class InfrastructureModule {}
