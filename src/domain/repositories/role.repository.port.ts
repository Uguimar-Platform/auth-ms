import { Role } from '@prisma/client';
import { RoleModel } from '../models/role.model';

export interface RoleRepositoryPort {
  createRole(role: RoleModel);
  findRoleById(id: number);
  updateRole(id: number, module: RoleModel);
  updateRoleStatus(id: number, status: number);
  listRolePaginated(filter: RoleModel);
}
