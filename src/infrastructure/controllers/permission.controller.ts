import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
} from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { CreatedPermissionsUseCase } from 'src/application/use-cases/permissions/create-permissions.usecase';
import { CreatePermissionDto } from '../dto/permissions/create-permissions.dto';
import { PaginatedFilterDto } from '../dto/list-module.filter.dto';
import { ListPermissionsPaginatedUseCase } from 'src/application/use-cases/permissions/list-permissions-paginated.usecase';
import { UpdatePermissionUseCase } from 'src/application/use-cases/permissions/update-permission.usecase';
import { UpdatePermissionDto } from '../dto/permissions/update-permission.dto';

@Controller('permissions')
export class PermissionController {
  constructor(
    private readonly createPermissionUseCase: CreatedPermissionsUseCase,
    private readonly listPermissionsPaginatedUseCase: ListPermissionsPaginatedUseCase,
    private readonly updatePermissionUseCase: UpdatePermissionUseCase,
  ) {}

  @MessagePattern({ cmd: 'create_permission' })
  async createPermision(@Payload() permission: CreatePermissionDto) {
    const created = await this.createPermissionUseCase.execute(permission);
    return created;
  }

  @MessagePattern({ cmd: 'list_permission_paginated' })
  async list(@Payload() paginated: PaginatedFilterDto) {
    return await this.listPermissionsPaginatedUseCase.execute(paginated);
  }

  @MessagePattern({ cmd: 'update_permission' })
  async update(
    @Payload() payload: UpdatePermissionDto,
    @Payload('id') id: number,
  ) {
    return await this.updatePermissionUseCase.execute(id, payload);
  }
}
