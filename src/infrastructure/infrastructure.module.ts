/*
https://docs.nestjs.com/modules
*/

import { Module } from '@nestjs/common';
import { ApplicationModule } from 'src/application/application.module';
import { UserController } from './controllers/user.controller';
import { ModuleController } from './controllers/module.controller';

@Module({
  imports: [ApplicationModule],
  controllers: [UserController, ModuleController],
  providers: [],
})
export class InfrastructureModule {}
