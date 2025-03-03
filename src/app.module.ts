import { UserController } from './infrastructure/controllers/user.controller';
import { InfrastructureModule } from './infrastructure/infrastructure.module';
import { DomainModule } from './domain/domain.module';
import { ApplicationModule } from './application/application.module';
import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';

@Module({
  imports: [InfrastructureModule, DomainModule, ApplicationModule],
  controllers: [UserController, AppController],
  providers: [AppService],
})
export class AppModule {}
