/*
https://docs.nestjs.com/controllers#controllers
*/

import { Body, Controller, HttpStatus, Post, Res } from '@nestjs/common';
import { CreateUserUseCase } from 'src/application/use-cases/create-user.usecase';
import { CreateUserDTO } from '../dto/create-user.dto';
import { MessagePattern, Payload } from '@nestjs/microservices';

@Controller()
export class UserController {
  constructor(private readonly createUserUseCase: CreateUserUseCase) {}

  @MessagePattern('auth.register.user')
  async createAuthor(
    @Res() request,
    @Payload() user: CreateUserDTO,
  ): Promise<any> {
    const authorCreated = await this.createUserUseCase.execute(user);
    return request.status(HttpStatus.CREATED).json(authorCreated);
  }
}
