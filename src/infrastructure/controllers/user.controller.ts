/*
https://docs.nestjs.com/controllers#controllers
*/

import { Controller, HttpStatus, Post, Res } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { RegisterUserDto } from '../dto/register-user.dto';
import { RegisterUserUseCase } from 'src/application/use-cases/users/register-user.usecase';

@Controller()
export class UserController {
  constructor(private readonly registerUserUseCase: RegisterUserUseCase) {}

  @MessagePattern('auth.register.user')
  async createAuthor(@Payload() user: RegisterUserDto) {
    const userCreated = await this.registerUserUseCase.execute(user);
    return {
      status: HttpStatus.CREATED,
      data: userCreated,
    };
  }
}
