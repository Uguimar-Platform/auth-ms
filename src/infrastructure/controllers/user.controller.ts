/*
https://docs.nestjs.com/controllers#controllers
*/

import { Controller, HttpStatus, Post, Res } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { RegisterUserDto } from '../dto/register-user.dto';
import { RegisterUserUseCase } from 'src/application/use-cases/users/register-user.usecase';
import { LoginUserUseCase } from 'src/application/use-cases/users/login.usecase';

@Controller()
export class UserController {
  constructor(
    private readonly registerUserUseCase: RegisterUserUseCase,
    private readonly loginUserUseCase: LoginUserUseCase,
  ) {}

  @MessagePattern('auth.register.user')
  async createAuthor(@Payload() user: RegisterUserDto) {
    const userCreated = await this.registerUserUseCase.execute(user);
    return {
      status: HttpStatus.CREATED,
      data: userCreated,
    };
  }

  @MessagePattern('auth.login.user')
  async login(@Payload() data: { email: string; password: string }) {
    const result = await this.loginUserUseCase.execute(
      data.email,
      data.password,
    );
    return {
      status: HttpStatus.OK,
      data: result,
    };
  }
}
