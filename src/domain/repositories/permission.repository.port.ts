import { PaginatedFilterDto } from 'src/infrastructure/dto/list-module.filter.dto';
import { PermissionsModel } from '../models/permissions.model';

export interface PermissionsRepositoryPort {
  createPermission(permission: PermissionsModel);
  findPermissionById(id: number);
  updatePermission(id: number, permission: Partial<PermissionsModel>);
  updatePermissionStatus(id: number, status: number);
  listPermissionPaginated(filter: PaginatedFilterDto);
}
