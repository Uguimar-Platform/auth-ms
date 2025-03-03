/*
https://docs.nestjs.com/modules
*/

import { Module } from '@nestjs/common';
import { ApplicationModule } from 'src/application/application.module';
import { UserController } from './controllers/user.controller';

@Module({
  imports: [ApplicationModule],
  controllers: [UserController],
  providers: [],
})
export class InfrastructureModule {}
