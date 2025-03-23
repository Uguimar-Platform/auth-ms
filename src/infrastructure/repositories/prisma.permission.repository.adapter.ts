import { Injectable } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import { PermissionsModel } from 'src/domain/models/permissions.model';
import { PermissionsRepositoryPort } from 'src/domain/repositories/permission.repository.port';

@Injectable()
export class PrismaPermissionRepositoryAdapter
  implements PermissionsRepositoryPort
{
  constructor(private prisma: PrismaClient) {}

  async createPermission(permisision: PermissionsModel) {
    const createPermission = await this.prisma.permission.create({
      data: permisision,
    });

    return createPermission;
  }

  async findPermissionById(id: number) {
    
  }
  async updatePermission(id: number, module: PermissionsModel) {
    throw new Error('Method not implemented.');
  }
  async updatePermissionStatus(id: number, status: number) {
    throw new Error('Method not implemented.');
  }
  async listPermissionPaginated(filter: PermissionsModel) {
    throw new Error('Method not implemented.');
  }
}
