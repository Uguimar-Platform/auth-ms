import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { Transport } from '@nestjs/microservices';
import { envs } from './config';
import { Logger } from '@nestjs/common';

async function bootstrap() {

  const logger = new Logger('Auth-Microservice');

  console.log(envs.natsServers);
  const app = await NestFactory.createMicroservice(AppModule, {
    transport: Transport.NATS,
    options: {
      servers: envs.natsServers,
    },
  });
  await app.listen();
  logger.log(`Auth-Microservice is running on: ${envs.port}`);
}
bootstrap();
  
