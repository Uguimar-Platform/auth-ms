import { PaginatedFilterDto } from 'src/infrastructure/dto/list-module.filter.dto';
import { RoleModel } from '../models/role.model';

export interface RoleRepositoryPort {
  createRole(role: RoleModel);
  findRoleById(id: number);
  updateRole(id: number, role: Partial<RoleModel>);
  updateRoleStatus(id: number, status: number);
  listRolePaginated(filter: PaginatedFilterDto);
}
