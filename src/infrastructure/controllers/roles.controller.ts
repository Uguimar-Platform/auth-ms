import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  ParseIntPipe,
  Patch,
  Post,
} from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { CreatedPermissionsUseCase } from 'src/application/use-cases/permissions/create-permissions.usecase';
import { CreatePermissionDto } from '../dto/permissions/create-permissions.dto';
import { CreateRoleUseCase } from 'src/application/use-cases/roles/create-role.usecase';
import { RoleModel } from 'src/domain/models/role.model';
import { FindRoleByIdUseCase } from 'src/application/use-cases/roles/find-role-by-id.usecase';
import { UpdateRoleUseCase } from 'src/application/use-cases/roles/update-role.usecase';
import { UpdateStatusRoleUseCase } from 'src/application/use-cases/roles/update-status.usecase';
import { UpdateRoleDto } from '../dto/roles/update-role.dto';
import { PaginatedFilterDto } from '../dto/list-module.filter.dto';
import { ListRolesPaginatedUseCase } from 'src/application/use-cases/roles/list-roles-paginated.usecase';

@Controller('roles')
export class RolesController {
  constructor(
    private readonly createRoleUseCase: CreateRoleUseCase,
    private readonly findRoleByIdUseCase: FindRoleByIdUseCase,
    private readonly updateRoleUseCase: UpdateRoleUseCase,
    private readonly updateStatusRoleUseCase: UpdateStatusRoleUseCase,
    private readonly listRolesPaginatedUseCase: ListRolesPaginatedUseCase,
  ) {}

  /*@Post()
  create(@Body() createPermissionDto: CreatePermissionDto) {
    return this.permissionService.create(createPermissionDto);
  }*/

  @MessagePattern({ cmd: 'create_role' })
  async createPermision(@Payload() role: RoleModel) {
    const created = await this.createRoleUseCase.execute(role);
    return created;
  }

  /**
   *
   * @param id The id of the module to be found.
   * @returns The module found.
   *
   */
  @MessagePattern({ cmd: 'find_role_id' })
  async findOne(@Payload('id', ParseIntPipe) id: number) {
    return this.findRoleByIdUseCase.execute(id);
  }

  @MessagePattern({ cmd: 'update_role' })
  async update(@Payload() payload: UpdateRoleDto, @Payload('id') id: number) {
    return await this.updateRoleUseCase.execute(id, payload);
  }

  @MessagePattern({ cmd: 'update_role_status' })
  async updateStatus(@Payload() payload: { id: number; status: number }) {
    const { id, status } = payload;
    return await this.updateStatusRoleUseCase.execute(id, status);
  }

  @MessagePattern({ cmd: 'list_role_paginated' })
  async list(@Payload() filter: PaginatedFilterDto) {
    return await this.listRolesPaginatedUseCase.execute(filter);
  }
}
