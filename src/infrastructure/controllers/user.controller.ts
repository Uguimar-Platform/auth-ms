/*
https://docs.nestjs.com/controllers#controllers
*/

import { Controller, HttpStatus, Post, Res } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { RegisterUserDto } from '../dto/user/register-user.dto';
import { RegisterUserUseCase } from 'src/application/use-cases/users/register-user.usecase';
import { LoginUserUseCase } from 'src/application/use-cases/users/login.usecase';
import { UpdateUserDto } from '../dto/user/update-user.dto';
import { UpdateUserUseCase } from 'src/application/use-cases/users/update-user.usecase';
import { UpdateStatusUserUseCase } from 'src/application/use-cases/users/update-status.usecase';
import { PaginatedFilterDto } from '../dto/list-module.filter.dto';
import { ListUserPaginatedUseCase } from 'src/application/use-cases/users/list-user.usecase';

@Controller()
export class UserController {
  constructor(
    private readonly registerUserUseCase: RegisterUserUseCase,
    private readonly loginUserUseCase: LoginUserUseCase,
    private readonly updateUserUseCase: UpdateUserUseCase,
    private readonly updateStatusUserUseCase: UpdateStatusUserUseCase,
    private readonly listUserPaginatedUseCase: ListUserPaginatedUseCase,
  ) {}

  @MessagePattern('auth.register.user')
  async createAuthor(@Payload() user: RegisterUserDto) {
    const userCreated = await this.registerUserUseCase.execute(user);
    return userCreated;

  }

  @MessagePattern('auth.login.user')
  async login(@Payload() data: { email: string; password: string }) {
    const result = await this.loginUserUseCase.execute(
      data.email,
      data.password,
    );
    return result;
  }

  @MessagePattern({ cmd: 'auth.update.user' })
  async update(@Payload() payload: UpdateUserDto, @Payload('id') id: number) {
    return await this.updateUserUseCase.execute(id, payload);
  }

  @MessagePattern({ cmd: 'update_user_status' })
  async updateStatus(@Payload() payload: { id: number; status: number }) {
    const { id, status } = payload;
    return await this.updateStatusUserUseCase.execute(id, status);
  }

  @MessagePattern({ cmd: 'list_user_paginated' })
  async list(@Payload() paginated: PaginatedFilterDto) {
    return await this.listUserPaginatedUseCase.execute(paginated);
  }
}
