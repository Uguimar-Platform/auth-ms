import { PermissionsModel } from '../models/permissions.model';

export interface PermissionsRepositoryPort {
  createPermission(permisison: PermissionsModel);
  findPermissionById(id: number);
  updatePermission(id: number, module: PermissionsModel);
  updatePermissionStatus(id: number, status: number);
  listPermissionPaginated(filter: PermissionsModel);
}
